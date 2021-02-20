package space.gavinklfong.forex.controllers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.TradeDealReq;
import space.gavinklfong.forex.services.TradeService;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.TradeDeal;

@WebFluxTest(controllers = {TradeDealRestController.class})
@Tag("UnitTest")
public class TradeDealRestControllerTest {

	@MockBean
	private TradeService tradeService;
	
	@Autowired
	WebTestClient webTestClient;
	
	@DisplayName("submitDeal - Success case")
	@Test
	public void submitDeal() throws Exception {

		when(tradeService.postTradeDeal(any(TradeDealReq.class)))
		.thenAnswer(invocation -> {
			TradeDealReq req = (TradeDealReq)invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Mono.just(new TradeDeal(1l, UUID.randomUUID().toString(),  timestamp, req.getBaseCurrency(), req.getCounterCurrency(),
					 req.getRate(), req.getBaseCurrencyAmount(), new Customer(1l, "Tester 1", 1)));
		});
			
		TradeDealReq req = new TradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		webTestClient.post()
		.uri("/deals")
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), TradeDealReq.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody(TradeDeal.class);
	}
	
	@DisplayName("submitDeal - Invalid Req")
	@Test
	public void submitDeal_invalidReq() throws Exception {

		when(tradeService.postTradeDeal(any(TradeDealReq.class)))
		.thenAnswer(invocation -> {
			TradeDealReq req = (TradeDealReq)invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Mono.just(new TradeDeal(1l, UUID.randomUUID().toString(),  timestamp, req.getBaseCurrency(), req.getCounterCurrency(),
					 req.getRate(), req.getBaseCurrencyAmount(), new Customer(1l, "Tester 1", 1)));
		});
			
		TradeDealReq req = new TradeDealReq();
		
		webTestClient.post()
		.uri("/deals")
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), TradeDealReq.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorBody.class);
	}
	
	@DisplayName("getDeal - Success case")
	@Test
	public void getDeals() throws Exception {

		TradeDeal deal1 = new TradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), new Customer(1l, "Tester 1", 1));
		TradeDeal deal2 = new TradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), new Customer(1l, "Tester 1", 1));
		TradeDeal deal3 = new TradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), new Customer(1l, "Tester 1", 1));
				
		when(tradeService.retrieveTradeDealByCustomer((anyLong())))
		.thenReturn(Flux.just(deal1, deal2, deal3));
		
		webTestClient.get()
		.uri(uriBuilder -> uriBuilder
				.path("/deals")
				.queryParam("customerId", 1)
				.build()
				)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk();
	}
	
}
