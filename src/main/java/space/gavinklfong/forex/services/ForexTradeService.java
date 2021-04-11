package space.gavinklfong.forex.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.exceptions.InvalidRateBookingException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.models.ForexTradeDeal;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.ForexTradeDealRepo;

@Slf4j
@Component
public class ForexTradeService {

	@Autowired
	private ForexTradeDealRepo tradeDealRepo;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private ForexRateService rateService;
	
	/**
	 * This method process forex trade deal submission. It carries out the following validation:
	 *  1. Check if customer id exists in customer repository
	 *  2. Invoke Rate Service for validation of rate booking
	 *  
	 *  If everything is fine, then trade deal record will be assigned a unique deal reference save to repository
	 * 
	 * @param req - Java bean containing trade deal request
	 * @return Trade Deal Record with unique deal reference wrapped in Mono type
	 */
	public Mono<ForexTradeDeal> postTradeDeal(ForexTradeDealReq req) {
		
		// Validate customer id
		Mono<Customer> customer = customerRepo.findById(req.getCustomerId());
		if (customer.blockOptional().isEmpty()) return Mono.error(new UnknownCustomerException());
			
		ForexRateBooking rateBooking = new ForexRateBooking(req.getBaseCurrency(), req.getCounterCurrency(), req.getRate(), req.getBaseCurrencyAmount(), req.getRateBookingRef());
		
		// Validate rate booking
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
	
		return result.flatMap(isValid -> {
			if (isValid) {
				// build and save trade deal record
				String dealRef = UUID.randomUUID().toString();
				LocalDateTime timestamp = LocalDateTime.now();
				ForexTradeDeal deal = new ForexTradeDeal(dealRef, timestamp, req.getBaseCurrency(), req.getCounterCurrency(), 
						req.getRate(), req.getBaseCurrencyAmount(), new Customer(req.getCustomerId()));
				
				return tradeDealRepo.save(deal);
				
			} else {
				// throw exception if rate booking is invalid
				return Mono.error(new InvalidRateBookingException());
			}
		});

	}
	
	/**
	 * Fetch trade deal by customer id
	 * 
	 * @param customerId 
	 * @return List of trade deal wrapped in Flux data type
	 */
	public Flux<ForexTradeDeal> retrieveTradeDealByCustomer(Long customerId) {
		
		return tradeDealRepo.findByCustomerId(customerId);

	}
	
}
