package space.gavinklfong.forex.services;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import space.gavinklfong.forex.models.TradeDeal;
import space.gavinklfong.forex.dto.TradeDealReq;
import space.gavinklfong.forex.exceptions.InvalidRateBookingException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.TradeDealRepo;

@SpringJUnitConfig
@ContextConfiguration(classes = {TradeService.class})
@Tag("UnitTest")
public class TradeServiceTest {

	@MockBean
	private CustomerRepo customerRepo;
	
	@MockBean
	private TradeDealRepo tradeDealRepo;
	
	@MockBean
	private RateService rateService;
	
	@Autowired
	private TradeService tradeService;
	
	@Test
	public void postTradeDealTest_success() {
		
		when(tradeDealRepo.save(any(TradeDeal.class))).thenAnswer(invocation -> {
			TradeDeal deal = (TradeDeal) invocation.getArgument(0);
			deal.setId(99l);
			return deal;
		});
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.of(new Customer(1l, "Tester 1", 1)));
		
		when(rateService.validateRateBooking(any(RateBooking.class))).thenReturn(Mono.just(true));
				
		TradeDealReq req = new TradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		Mono<TradeDeal> deal = tradeService.postTradeDeal(req);
		StepVerifier.create(deal)
		.expectNextMatches(record -> record.getId() == 99l)
		.verifyComplete();		
	}
	
	@Test
	public void postTradeDealTest_invalidRateBooking() {
		
		when(tradeDealRepo.save(any(TradeDeal.class))).thenAnswer(invocation -> {
			TradeDeal deal = (TradeDeal) invocation.getArgument(0);
			deal.setId(99l);
			return deal;
		});
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.of(new Customer(1l, "Tester 1", 1)));
		
		when(rateService.validateRateBooking(any(RateBooking.class))).thenReturn(Mono.just(false));
				
		TradeDealReq req = new TradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		Mono<TradeDeal> deal = tradeService.postTradeDeal(req);
		StepVerifier.create(deal)
		.expectError(InvalidRateBookingException.class)
		.verify();
	}
	
	@Test
	public void postTradeDealTest_unknownCustomer() {
		
		when(tradeDealRepo.save(any(TradeDeal.class))).thenAnswer(invocation -> {
			TradeDeal deal = (TradeDeal) invocation.getArgument(0);
			deal.setId(99l);
			return deal;
		});
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.empty());
		
		when(rateService.validateRateBooking(any(RateBooking.class))).thenReturn(Mono.just(true));
				
		TradeDealReq req = new TradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		
		Mono<TradeDeal> deal = tradeService.postTradeDeal(req);
		StepVerifier.create(deal)
		.expectError(UnknownCustomerException.class)
		.verify();
	}
	
	@Test
	public void retrieveTradeDealByCustomerTest() {
		
		TradeDeal deal1 = new TradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), new Customer(1l, "Tester 1", 1));
		TradeDeal deal2 = new TradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), new Customer(1l, "Tester 1", 1));
		TradeDeal deal3 = new TradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), new Customer(1l, "Tester 1", 1));
				
		List<TradeDeal> deals = new ArrayList<>();
		deals.add(deal1);
		deals.add(deal2);
		deals.add(deal3);
		
		when(tradeDealRepo.findByCustomerId(anyLong())).thenReturn(deals);
		
		Flux<TradeDeal> result = tradeService.retrieveTradeDealByCustomer(1l);
		
		StepVerifier
		.create(result)
		.expectNext(deal1)
		.expectNext(deal2)
		.expectNext(deal3)
		.expectComplete()
		.verify();
	}
	
	@Test
	public void retrieveTradeDealByCustomerTest_Empty() {
		
		when(tradeDealRepo.findByCustomerId(anyLong())).thenReturn(null);
		
		Flux<TradeDeal> result = tradeService.retrieveTradeDealByCustomer(1l);
		
		StepVerifier
		.create(result)
		.expectComplete()
		.verify();

	}
	
}
