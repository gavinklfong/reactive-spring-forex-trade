package space.gavinklfong.forex.services;

import java.math.BigDecimal;
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
	
	
	public Mono<TradeDeal> postTradeDeal(TradeDealReq req) {
		
		Optional<Customer> customer = customerRepo.findById(req.getCustomerId());
		if (customer.isEmpty()) throw new UnknownCustomerException();
		
		RateBooking rateBooking = new RateBooking(req.getBaseCurrency(), req.getCounterCurrency(), req.getRate(), req.getRateBookingRef());
		
		// Validate rate booking
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
	
		return result.flatMap(isValid -> {
			
			if (isValid) {
				
				// build trade deal record
				String dealRef = UUID.randomUUID().toString();
				LocalDateTime timestamp = LocalDateTime.now();
				TradeDeal deal = new TradeDeal(dealRef, timestamp, req.getBaseCurrency(), req.getCounterCurrency(), 
						req.getRate(), req.getBaseCurrencyAmount(), new Customer(req.getCustomerId()));
				
				return Mono.just(tradeDealRepo.save(deal));
				
			} else {
				return Mono.error(new InvalidRateBookingException());
				
			}
		});

	}
	
	public Flux<TradeDeal> retrieveTradeDealByCustomer(Long customerId) {
		
		List<TradeDeal> deals = tradeDealRepo.findByCustomerId(customerId);
		
		if (deals == null || deals.isEmpty()) 
			return Flux.empty();
		else
			return Flux.fromStream(deals.stream());
	}
	
}
