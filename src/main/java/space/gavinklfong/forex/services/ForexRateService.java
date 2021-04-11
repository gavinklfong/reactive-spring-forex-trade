package space.gavinklfong.forex.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexRate;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.ForexRateBookingRepo;

@Slf4j
@Component
public class ForexRateService {
	
	@Value("${app.rate-booking-duration}")
	private long bookingDuration = 120l;
		
	@Autowired
	private ForexRateApiClient forexRateApiClient;
	
	@Autowired
	private ForexRateBookingRepo rateBookingRepo;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private ForexPriceService forexPriceService;
	
	/**
	 * Retrieve the latest rates for list of counter currencies
	 * 
	 * @param baseCurrency
	 * @return Flux - Rate
	 */
	public Flux<ForexRate> fetchLatestRates(String baseCurrency) {
		
		LocalDateTime timestamp = LocalDateTime.now();
		
		return forexRateApiClient.fetchLatestRates(baseCurrency)
		.flatMapMany(resp -> { 
			
			List<ForexRate> rates = new ArrayList<>();
			
			resp.getRates().forEach((counterCurrency, rate) -> {
				rates.add(new ForexRate(timestamp, baseCurrency, counterCurrency, 
						forexPriceService.obtainForexPrice(baseCurrency, counterCurrency, rate)));
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
	public Mono<ForexRateBooking> obtainBooking(ForexRateBookingReq request) throws UnknownCustomerException {

		// retrieve customer tier
		log.debug("Retrieve customer record - id = " + request.getCustomerId());
		Mono<Customer> customer = customerRepo.findById(request.getCustomerId());
//		if (customer.blockOptional().isEmpty()) throw new UnknownCustomerException();
				
		// fetch rate from external API
		log.debug("fetch rate from external API");
		return forexRateApiClient.fetchLatestRates(request.getBaseCurrency(), request.getCounterCurrency())
				.flatMap(record -> {
					double rate = forexPriceService.obtainForexPrice(request.getBaseCurrency(), request.getCounterCurrency(), record.getRates().get(request.getCounterCurrency()));
					return constructRateBooking(request, customer, rate);
				});
				
	}
	
	private Mono<ForexRateBooking> constructRateBooking(ForexRateBookingReq request, Mono<Customer> customer, Double rate) {
		
		return customer.log()
				.map(c -> {
					
					double r = rate;
					
					// determine rate
					switch (c.getTier()) {
					case 1:
						r += CustomerRateTier.TIER1.rate;
						break;
					case 2:
						r += CustomerRateTier.TIER2.rate;
						break;
					case 3:
						r += CustomerRateTier.TIER3.rate;
						break;
					default:
						r += CustomerRateTier.TIER4.rate;
					}
					return rate;
				})
				.log()
				.flatMap(r -> {
					ForexRateBooking bookingRecord = new ForexRateBooking(request.getBaseCurrency(), request.getCounterCurrency(), request.getBaseCurrencyAmount(), request.getCustomerId());

					UUID bookingRef = UUID.randomUUID();
					bookingRecord.setBookingRef(bookingRef.toString());

					LocalDateTime timestamp = LocalDateTime.now();
					bookingRecord.setTimestamp(timestamp);

					LocalDateTime expiryTime = timestamp.plusSeconds(bookingDuration);
					bookingRecord.setExpiryTime(expiryTime);
					
					bookingRecord.setRate(r);

					// save rate booking to repo
					log.debug("save rate booking to repo");
					
					return rateBookingRepo.save(bookingRecord);
				});
				
		
//		log.debug("customer: " + customer.getId() + ", " + customer.getName() + ", " + customer.getTier());
//		
//		// determine rate
//		switch (customer.getTier()) {
//		case 1:
//			rate += CustomerRateTier.TIER1.rate;
//			break;
//		case 2:
//			rate += CustomerRateTier.TIER2.rate;
//			break;
//		case 3:
//			rate += CustomerRateTier.TIER3.rate;
//			break;
//		default:
//			rate += CustomerRateTier.TIER4.rate;
//		}
//		
//		
//		// build rate booking record
//		log.debug("build rate booking record - rate = " + rate);
//		ForexRateBooking bookingRecord = new ForexRateBooking(request.getBaseCurrency(), request.getCounterCurrency(), request.getBaseCurrencyAmount(), request.getCustomerId());
//
//		UUID bookingRef = UUID.randomUUID();
//		bookingRecord.setBookingRef(bookingRef.toString());
//
//		LocalDateTime timestamp = LocalDateTime.now();
//		bookingRecord.setTimestamp(timestamp);
//
//		LocalDateTime expiryTime = timestamp.plusSeconds(bookingDuration);
//		bookingRecord.setExpiryTime(expiryTime);
//		
//		bookingRecord.setRate(rate);
//
//		// save rate booking to repo
//		log.debug("save rate booking to repo");
//		
//		return rateBookingRepo.save(bookingRecord);
	}
	
	/**
	 * Validate whether Rate Booking by
	 *  1. Check if booking reference exists in repository
	 *  2. Check if the record is expired
	 * 
	 * @param rateBooking
	 * @return
	 */
	public Mono<Boolean> validateRateBooking(ForexRateBooking rateBooking) {
		
		// Check existence of booking reference
		Flux<ForexRateBooking> repoRecords = rateBookingRepo.findByBookingRef(rateBooking.getBookingRef());
		
		
//		if (repoRecords == null || repoRecords.size() <= 0) {
//			return Mono.just(false);
//		}
		
		// Check if booking reference is expired
		
		return repoRecords
		.map(record -> {
			if (record.getExpiryTime().isBefore(LocalDateTime.now()) ||
					record.getBaseCurrencyAmount().compareTo(rateBooking.getBaseCurrencyAmount()) != 0) {
				return false;
			} else {
				return true;
			}
		})
		.single();
		
	}
}
