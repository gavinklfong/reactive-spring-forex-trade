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
import space.gavinklfong.forex.models.ForexTradeDeal;
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.exceptions.InvalidRateBookingException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.ForexTradeDealRepo;

@SpringJUnitConfig
@ContextConfiguration(classes = {ForexTradeService.class})
@Tag("UnitTest")
public class ForexTradeServiceTest {

	@MockBean
	private CustomerRepo customerRepo;
	
	@MockBean
	private ForexTradeDealRepo tradeDealRepo;
	
	@MockBean
	private ForexRateService rateService;
	
	@Autowired
	private ForexTradeService tradeService;
	
	@Test
	public void postTradeDealTest_success() {
		
		when(tradeDealRepo.save(any(ForexTradeDeal.class))).thenAnswer(invocation -> {
			ForexTradeDeal deal = (ForexTradeDeal) invocation.getArgument(0);
			deal.setId(99l);
			return deal;
		});
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Mono.just(new Customer(1l, "Tester 1", 1)));
		
		when(rateService.validateRateBooking(any(ForexRateBooking.class))).thenReturn(Mono.just(true));
				
		ForexTradeDealReq req = new ForexTradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		Mono<ForexTradeDeal> deal = tradeService.postTradeDeal(req);
		StepVerifier.create(deal)
		.expectNextMatches(record -> record.getId() == 99l)
		.verifyComplete();		
	}
	
	@Test
	public void postTradeDealTest_invalidRateBooking() {
		
		when(tradeDealRepo.save(any(ForexTradeDeal.class))).thenAnswer(invocation -> {
			ForexTradeDeal deal = (ForexTradeDeal) invocation.getArgument(0);
			deal.setId(99l);
			return deal;
		});
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Mono.just(new Customer(1l, "Tester 1", 1)));
		
		when(rateService.validateRateBooking(any(ForexRateBooking.class))).thenReturn(Mono.just(false));
				
		ForexTradeDealReq req = new ForexTradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		Mono<ForexTradeDeal> deal = tradeService.postTradeDeal(req);
		StepVerifier.create(deal)
		.expectError(InvalidRateBookingException.class)
		.verify();
	}
	
	@Test
	public void postTradeDealTest_unknownCustomer() {
		
		when(tradeDealRepo.save(any(ForexTradeDeal.class))).thenAnswer(invocation -> {
			ForexTradeDeal deal = (ForexTradeDeal) invocation.getArgument(0);
			deal.setId(99l);
			return deal;
		});
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Mono.empty());
		
		when(rateService.validateRateBooking(any(ForexRateBooking.class))).thenReturn(Mono.just(true));
				
		ForexTradeDealReq req = new ForexTradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		
		Mono<ForexTradeDeal> deal = tradeService.postTradeDeal(req);
		StepVerifier.create(deal)
		.expectError(UnknownCustomerException.class)
		.verify();
	}
	
	@Test
	public void retrieveTradeDealByCustomerTest() {
		
		ForexTradeDeal deal1 = new ForexTradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), 1l);
		ForexTradeDeal deal2 = new ForexTradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), 1l);
		ForexTradeDeal deal3 = new ForexTradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), 1l);
				
		List<ForexTradeDeal> deals = new ArrayList<>();
		deals.add(deal1);
		deals.add(deal2);
		deals.add(deal3);
		
		when(tradeDealRepo.findByCustomerId(anyLong())).thenReturn(Flux.fromIterable(deals) );
		
		Flux<ForexTradeDeal> result = tradeService.retrieveTradeDealByCustomer(1l);
		
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
		
		Flux<ForexTradeDeal> result = tradeService.retrieveTradeDealByCustomer(1l);
		
		StepVerifier
		.create(result)
		.expectComplete()
		.verify();

	}
	
}
