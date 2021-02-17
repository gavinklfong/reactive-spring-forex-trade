package space.gavinklfong.forex.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

	@Value("${app.rate-booking-duration}")
	private long bookingDuration = 120l;
	
	@Value("${app.forex-rate-api-url}")
	private String forexApiUrl;
	
	private WebClient forexRateApiClient = WebClient.create(forexApiUrl);

	
	@Autowired
	private RateBookingRepo rateBookingRepo;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	/**
	 * Retrieve the latest rates for list of counter currencies
	 * 
	 * @param baseCurrency
	 * @return
	 */
	public Flux<Rate> fetchLatestRates(String baseCurrency) {
		
		return Flux.just(new Rate());
	}
	
	/**
	 * Generate rate booking based on the latest rate and customer tier
	 * 
	 * @param request
	 * @return
	 */
	public Mono<RateBooking> obtainBooking(RateBookingReq request) throws UnknownCustomerException {

		// fetch rate from external API
//		Mono<String> response =  
//				forexRateApiClient.get().uri("/latest?base={base}", "GBP")
//				.accept(MediaType.APPLICATION_JSON)
//				.retrieve()
//				.bodyToMono(String.class);
		
		// retrieve customer tier
		Optional<Customer> customer = customerRepo.findById(request.getCustomerId());
		if (customer.isEmpty()) throw new UnknownCustomerException();
		
		// determine rate
		double rate = 0.25;
		if (customer.get().getTier() > 0) {
			rate = 0.5;
		}
		
		// build rate booking record
		RateBooking bookingRecord = new RateBooking(request.getBaseCurrency(), request.getCounterCurrency(), request.getCustomerId());

		UUID bookingRef = UUID.randomUUID();
		LocalDateTime timestamp = LocalDateTime.now();
		LocalDateTime expiryTime = timestamp.plusSeconds(bookingDuration);
		bookingRecord.setBookingRef(bookingRef.toString());
		bookingRecord.setTimestamp(timestamp);
		bookingRecord.setExpiryTime(expiryTime);
		bookingRecord.setRate(rate);

		
		return Mono.<RateBooking>fromCallable(() -> 
			 rateBookingRepo.save(bookingRecord)
		);
		
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
		
		if (repoRecords.size() <= 0) {
			return Mono.just(false);
		}
		
		// Check if booking reference is expired
		RateBooking record = repoRecords.get(0);
		if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
			return Mono.just(false);
		}
		
		return Mono.just(true);
		
	}
}
