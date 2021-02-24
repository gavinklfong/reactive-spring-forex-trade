package space.gavinklfong.forex.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.TradeDealReq;
import space.gavinklfong.forex.exceptions.InvalidRateBookingException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.models.TradeDeal;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.TradeDealRepo;


@Component
public class TradeService {

	@Autowired
	private TradeDealRepo tradeDealRepo;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private RateService rateService;
	
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
	public Mono<TradeDeal> postTradeDeal(TradeDealReq req) {
		
		// Validate customer id
		Optional<Customer> customer = customerRepo.findById(req.getCustomerId());
		if (customer.isEmpty()) return Mono.error(new UnknownCustomerException());
		
		RateBooking rateBooking = new RateBooking(req.getBaseCurrency(), req.getCounterCurrency(), req.getRate(), req.getBaseCurrencyAmount(), req.getRateBookingRef());
		
		// Validate rate booking
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
	
		return result.flatMap(isValid -> {
			if (isValid) {
				// build and save trade deal record
				String dealRef = UUID.randomUUID().toString();
				LocalDateTime timestamp = LocalDateTime.now();
				TradeDeal deal = new TradeDeal(dealRef, timestamp, req.getBaseCurrency(), req.getCounterCurrency(), 
						req.getRate(), req.getBaseCurrencyAmount(), new Customer(req.getCustomerId()));
				
				return Mono.just(tradeDealRepo.save(deal));
				
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
	public Flux<TradeDeal> retrieveTradeDealByCustomer(Long customerId) {
		
		List<TradeDeal> deals = tradeDealRepo.findByCustomerId(customerId);
		
		if (deals == null || deals.isEmpty()) 
			return Flux.empty();
		else
			return Flux.fromStream(deals.stream());
	}
	
}
