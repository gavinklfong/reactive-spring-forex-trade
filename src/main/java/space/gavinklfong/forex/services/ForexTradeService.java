package space.gavinklfong.forex.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.dto.TradeAction;
import space.gavinklfong.forex.exceptions.InvalidRateBookingException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
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
	 * Flow:
	 * Mono<Customer>			[Validate customer existence by retrieving record from repos]
	 * 							[Fire exception if data is empty]
	 * --> Mono<Boolean>		[Validate rate booking and return result as a boolean]
	 * --> Mono<ForexTradeDeal>	[Build and save a new trade deal record]
	 * 
	 * @param req - Java bean containing trade deal request
	 * @return Trade Deal Record with unique deal reference wrapped in Mono type
	 */
	public Mono<ForexTradeDeal> postTradeDeal(ForexTradeDealReq req) {
				
		return customerRepo.findById(req.getCustomerId())
				.switchIfEmpty(Mono.error(new UnknownCustomerException()))
				.flatMap(c -> {
					ForexRateBooking rateBooking = new ForexRateBooking(req.getBaseCurrency(), req.getCounterCurrency(), req.getRate(), req.getBaseCurrencyAmount(), req.getRateBookingRef());	
					log.info("validate rate booking");
					return rateService.validateRateBooking(rateBooking);
				})
				.flatMap(isValid -> {
					if (isValid) {
						// build and save trade deal record
						ForexTradeDeal deal = ForexTradeDeal.builder()
								.dealRef(UUID.randomUUID().toString())
								.timestamp(LocalDateTime.now())
								.baseCurrency(req.getBaseCurrency())
								.counterCurrency(req.getCounterCurrency())
								.rate(req.getRate())
								.baseCurrencyAmount(req.getBaseCurrencyAmount())
								.customerId(req.getCustomerId())
								.tradeAction(req.getTradeAction())
								.build();

						log.info("tradeDealRepo.save() {}", deal);

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
	public Flux<ForexTradeDeal> retrieveTradeDealByCustomer(Long customerId, Optional<String> dealRef) {

		if (dealRef.isPresent()) {
			return tradeDealRepo.findByCustomerIdAndDealRef(customerId, dealRef.get());
		} else {
			return tradeDealRepo.findByCustomerId(customerId);
		}
	}
	
}
