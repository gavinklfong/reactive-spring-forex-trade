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
import space.gavinklfong.forex.apiclients.ForexRateApiClient;
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
	 * Flow: 
	 * Mono<ApiResponse> 					[Retrieve from Forex Rate API] 
	 * --> Flux<Map.Entry<String, Double>>  [Extract rates from response]
	 * --> Flux<ForexRate> 					[Update rate based on price service]
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
	
	
	/**
	 * Generate rate booking based on the latest rate and customer tier
	 * 
	 * Flow:
	 * Mono<ApiResponse>					[Retrieve rate from forex API]
	 * --> Mono<ForexRate>					[Construct ForexRate]
	 * --> Mono<ForexRate>  			 	[Update rate by price service]
	 * --> Tuple[<ForexRate>, <Customer>] 	[Retrieve customer record and zip together]
	 * --> Mono<Forex>						[Adjust rate based on customer tier]
	 * --> Mono<ForexRateBooking>			[Build rate booking object]
	 * --> Mono<ForexRateBooking>			[Saved rate booking object with record id]
	 * 
	 * @param request
	 * @return Mono - RateBooking
	 */
	public Mono<ForexRateBooking> obtainBooking(ForexRateBookingReq request) throws UnknownCustomerException {
					
		LocalDateTime timestamp = LocalDateTime.now();
		
		return forexRateApiClient.fetchLatestRate(request.getBaseCurrency(), request.getCounterCurrency())
				.map(resp -> new ForexRate(timestamp, request.getBaseCurrency(), request.getCounterCurrency(), resp.getRates().get(request.getCounterCurrency())))
				.map(rate -> rate.withRate(forexPriceService.obtainForexPrice(request.getBaseCurrency(), request.getCounterCurrency(), rate.getRate())))
				.zipWith(customerRepo.findById(request.getCustomerId()))
				.map(tuple -> adjustRateForCustomerTier(tuple.getT1(), tuple.getT2()))	
				.map(rate -> buildRateBookingRecord(request, rate.getRate()))
				.flatMap(booking -> rateBookingRepo.save(booking));								
	}
		
	/**
	 * Validate whether Rate Booking by
	 *  1. Check if booking reference exists in repository
	 *  2. Check if the record is expired   
	 * 
	 * Flow:
	 * Flux<ForexRateBooking>		[Retrieve rate booking record from repos]
	 * --> Mono<ForexRateBooking>	[Convert Flux to Mono]
	 * --> Mono<Boolean>			[Validate rate booking record]
	 * 
	 * Return Mono<Boolean> false in case of exception such as no rate booking record found
	 * 
	 * @param rateBooking
	 * @return Validation result - true / false
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
				.onErrorReturn(Boolean.valueOf(false));

		
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
}
