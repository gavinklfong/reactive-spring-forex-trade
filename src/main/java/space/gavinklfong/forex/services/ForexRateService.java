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
		.flatMapMany(resp -> Flux.fromIterable(resp.getRates().entrySet()))
		.map(rate -> new ForexRate(timestamp, baseCurrency, rate.getKey(), 
						forexPriceService.obtainForexPrice(baseCurrency, rate.getKey(), rate.getValue()))	
		);
		
	}
	
	private Mono<ForexRate> fetchLatestRate(String baseCurrency, String counterCurrency) {
		
		LocalDateTime timestamp = LocalDateTime.now();
		
		return forexRateApiClient.fetchLatestRate(baseCurrency, counterCurrency)
		.map(resp -> 					
			new ForexRate(timestamp, baseCurrency, counterCurrency, resp.getRates().get(counterCurrency))	
		 )
		.map(r -> {
			Double value = forexPriceService.obtainForexPrice(
					baseCurrency, 
					counterCurrency,
					r.getRate()
					);
			
			return r.withRate(value);
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

				
		// fetch rate from external API
		log.debug("fetch rate from external API");
		return fetchLatestRate(request.getBaseCurrency(), request.getCounterCurrency())
				.map(rate -> rate.withRate(forexPriceService.obtainForexPrice(request.getBaseCurrency(), request.getCounterCurrency(), rate.getRate())))
				.zipWith(customer)
				.map(tuple -> adjustRateForCustomerTier(tuple.getT1(), tuple.getT2()))	
				.map(rate -> buildRateBookingRecord(request, rate.getRate()))
				.flatMap(booking -> rateBookingRepo.save(booking));
								
	}
	
	
	private ForexRate adjustRateForCustomerTier(ForexRate rate, Customer customer) {
		 	
			double adjustedRate = 0;
			
			// determine rate
			switch (customer.getTier()) {
			case 1:
				adjustedRate += CustomerRateTier.TIER1.rate;
				break;
			case 2:
				adjustedRate += CustomerRateTier.TIER2.rate;
				break;
			case 3:
				adjustedRate += CustomerRateTier.TIER3.rate;
				break;
			default:
				adjustedRate += CustomerRateTier.TIER4.rate;
			}
			
			return rate.withRate(adjustedRate);				
		
	}
	
	private ForexRateBooking buildRateBookingRecord(ForexRateBookingReq request, Double rate) {
		
		ForexRateBooking bookingRecord = new ForexRateBooking(request.getBaseCurrency(), request.getCounterCurrency(), request.getBaseCurrencyAmount(), request.getCustomerId());

		UUID bookingRef = UUID.randomUUID();
		bookingRecord.setBookingRef(bookingRef.toString());

		LocalDateTime timestamp = LocalDateTime.now();
		bookingRecord.setTimestamp(timestamp);

		LocalDateTime expiryTime = timestamp.plusSeconds(bookingDuration);
		bookingRecord.setExpiryTime(expiryTime);
		
		bookingRecord.setRate(rate);

		return bookingRecord;
				
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
					
		return rateBookingRepo.findByBookingRef(rateBooking.getBookingRef())
		.map(record -> {
			if (record.getExpiryTime().isBefore(LocalDateTime.now()) ||
					record.getBaseCurrencyAmount().compareTo(rateBooking.getBaseCurrencyAmount()) != 0) {
				return false;
			} else {
				return true;
			}
		})
		.single()
		.onErrorResume(e -> Mono.just(false));
		
	}
}
