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
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.services.ForexTradeService;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.ForexTradeDeal;

@WebFluxTest(controllers = {ForexTradeDealRestController.class})
@Tag("UnitTest")
public class ForexTradeDealRestControllerTest {

	@MockBean
	private ForexTradeService tradeService;
	
	@Autowired
	WebTestClient webTestClient;
	
	@DisplayName("submitDeal - Success case")
	@Test
	public void submitDeal() throws Exception {

		when(tradeService.postTradeDeal(any(ForexTradeDealReq.class)))
		.thenAnswer(invocation -> {
			ForexTradeDealReq req = (ForexTradeDealReq)invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Mono.just(new ForexTradeDeal(1l, UUID.randomUUID().toString(),  timestamp, req.getBaseCurrency(), req.getCounterCurrency(),
					 req.getRate(), req.getBaseCurrencyAmount(), 1l));
		});
			
		ForexTradeDealReq req = new ForexTradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		webTestClient.post()
		.uri("/deals")
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), ForexTradeDealReq.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody(ForexTradeDeal.class);
	}
	
	@DisplayName("submitDeal - Invalid Req")
	@Test
	public void submitDeal_invalidReq() throws Exception {

		when(tradeService.postTradeDeal(any(ForexTradeDealReq.class)))
		.thenAnswer(invocation -> {
			ForexTradeDealReq req = (ForexTradeDealReq)invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Mono.just(new ForexTradeDeal(1l, UUID.randomUUID().toString(),  timestamp, req.getBaseCurrency(), req.getCounterCurrency(),
					 req.getRate(), req.getBaseCurrencyAmount(), 1l));
		});
			
		ForexTradeDealReq req = new ForexTradeDealReq();
		
		webTestClient.post()
		.uri("/deals")
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), ForexTradeDealReq.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorBody.class);
	}
	
	@DisplayName("getDeal - Success case")
	@Test
	public void getDeals() throws Exception {

		ForexTradeDeal deal1 = new ForexTradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), 1l);
		ForexTradeDeal deal2 = new ForexTradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), 1l);
		ForexTradeDeal deal3 = new ForexTradeDeal(UUID.randomUUID().toString(), LocalDateTime.now(), "GBP", "USD",  Math.random(),
				BigDecimal.valueOf(1000), 1l);
				
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
