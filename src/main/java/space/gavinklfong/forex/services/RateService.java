package space.gavinklfong.forex.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.Rate;
import space.gavinklfong.forex.dto.RateBookingReq;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.RateBookingRepo;

@Component
public class RateService {

	private static Logger logger = LoggerFactory.getLogger(RateService.class);
	
	@Value("${app.rate-booking-duration}")
	private long bookingDuration = 120l;
		
	@Autowired
	private ForexRateApiClient forexRateApiClient;
	
	@Autowired
	private RateBookingRepo rateBookingRepo;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	/**
	 * Retrieve the latest rates for list of counter currencies
	 * 
	 * @param baseCurrency
	 * @return Flux - Rate
	 */
	public Flux<Rate> fetchLatestRates(String baseCurrency) {
		
		LocalDateTime timestamp = LocalDateTime.now();
		
		return forexRateApiClient.fetchLatestRates(baseCurrency)
		.flatMapMany(resp -> { 
			
			List<Rate> rates = new ArrayList<>();
			
			resp.getRates().forEach((counterCurrency, rate) -> {
				rates.add(new Rate(timestamp, baseCurrency, counterCurrency, rate));
			});
			
			return Flux.fromStream(rates.stream());
		});
		
	}
	
	/**
	 * Generate rate booking based on the latest rate and customer tier
	 * 
	 * @param request
	 * @return Mono - RateBooking
	 */
	public Mono<RateBooking> obtainBooking(RateBookingReq request) throws UnknownCustomerException {

		// retrieve customer tier
		logger.debug("Retrieve customer record - id = " + request.getCustomerId());
		Optional<Customer> customer = customerRepo.findById(request.getCustomerId());
		if (customer.isEmpty()) throw new UnknownCustomerException();
		
		// fetch rate from external API
		logger.debug("fetch rate from external API");
		return forexRateApiClient.fetchLatestRates(request.getBaseCurrency(), request.getCounterCurrency())
				.map(record -> 
						 generateRateBooking(request, customer.get(), record.getRates().get(request.getCounterCurrency()))
				);
				
	}
	
	private RateBooking generateRateBooking(RateBookingReq request, Customer customer, Double rate) {
		
		logger.debug("customer: " + customer.getId() + ", " + customer.getName() + ", " + customer.getTier());
		
		// determine rate
		switch (customer.getTier()) {
		case 1:
			rate += RateTier.TIER1.rate;
			break;
		case 2:
			rate += RateTier.TIER2.rate;
			break;
		case 3:
			rate += RateTier.TIER3.rate;
			break;
		default:
			rate += RateTier.TIER4.rate;
		}
		
		
		// build rate booking record
		logger.debug("build rate booking record - rate = " + rate);
		RateBooking bookingRecord = new RateBooking(request.getBaseCurrency(), request.getCounterCurrency(), request.getBaseCurrencyAmount(), request.getCustomerId());

		UUID bookingRef = UUID.randomUUID();
		bookingRecord.setBookingRef(bookingRef.toString());

		LocalDateTime timestamp = LocalDateTime.now();
		bookingRecord.setTimestamp(timestamp);

		LocalDateTime expiryTime = timestamp.plusSeconds(bookingDuration);
		bookingRecord.setExpiryTime(expiryTime);
		
		bookingRecord.setRate(rate);

		// save rate booking to repo
		logger.debug("save rate booking to repo");
		
		return rateBookingRepo.save(bookingRecord);
	}
	
	/**
	 * Validate whether Rate Booking by
	 *  1. Check if booking reference exists in repository
	 *  2. Check if the record is expired
	 * 
	 * @param rateBooking
	 * @return
	 */
	public Mono<Boolean> validateRateBooking(RateBooking rateBooking) {
		
		// Check existence of booking reference
		List<RateBooking> repoRecords = rateBookingRepo.findByBookingRef(rateBooking.getBookingRef());
		
		if (repoRecords == null || repoRecords.size() <= 0) {
			return Mono.just(false);
		}
		
		// Check if booking reference is expired
		RateBooking record = repoRecords.get(0);
		if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
			return Mono.just(false);
		}
		
		// Check if amount matches
		if (record.getBaseCurrencyAmount().compareTo(rateBooking.getBaseCurrencyAmount()) != 0) {
			return Mono.just(false);
		}
		
		
		return Mono.just(true);
		
	}
}
