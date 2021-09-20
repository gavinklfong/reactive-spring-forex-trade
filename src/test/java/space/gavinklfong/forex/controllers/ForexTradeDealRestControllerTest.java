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
import space.gavinklfong.forex.dto.TradeAction;
import space.gavinklfong.forex.services.ForexTradeService;
import space.gavinklfong.forex.exceptions.ErrorBody;
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
	void submitDeal() throws Exception {

		when(tradeService.postTradeDeal(any(ForexTradeDealReq.class)))
		.thenAnswer(invocation -> {
			ForexTradeDealReq req = (ForexTradeDealReq)invocation.getArgument(0);
			return Mono.just(
					ForexTradeDeal.builder()
					.id(1l).dealRef(UUID.randomUUID().toString())
					.timestamp(LocalDateTime.now())
					.baseCurrency(req.getBaseCurrency()).counterCurrency(req.getCounterCurrency())
					.rate(req.getRate()).baseCurrencyAmount(req.getBaseCurrencyAmount()).customerId(req.getCustomerId())
					.tradeAction(req.getTradeAction())
					.build()
				);
		});
			
		ForexTradeDealReq req = ForexTradeDealReq.builder()
				.tradeAction(TradeAction.BUY)
				.baseCurrency("GBP")
				.counterCurrency("USD")
				.rate(0.25)
				.baseCurrencyAmount(BigDecimal.valueOf(10000))
				.customerId(1l)
				.rateBookingRef("ABC")
				.build();				
		
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
	void submitDeal_invalidReq() throws Exception {

		when(tradeService.postTradeDeal(any(ForexTradeDealReq.class)))
		.thenAnswer(invocation -> {
			ForexTradeDealReq req = (ForexTradeDealReq)invocation.getArgument(0);
			return Mono.just(
					ForexTradeDeal.builder()
					.id(1l).dealRef(UUID.randomUUID().toString())
					.timestamp(LocalDateTime.now())
					.baseCurrency(req.getBaseCurrency()).counterCurrency(req.getCounterCurrency())
					.rate(req.getRate()).baseCurrencyAmount(req.getBaseCurrencyAmount()).customerId(req.getCustomerId())
					.tradeAction(req.getTradeAction())
					.build()
				);
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
	void getDeals() throws Exception {
				
		ForexTradeDeal deal1 = 
				ForexTradeDeal.builder()
				.id(1l).dealRef(UUID.randomUUID().toString())
				.timestamp(LocalDateTime.now())
				.baseCurrency("GBP").counterCurrency("USD")
				.rate(Math.random()).baseCurrencyAmount(BigDecimal.valueOf(1000)).customerId(1l)
				.tradeAction(TradeAction.BUY)
				.build();

		ForexTradeDeal deal2 = 
				ForexTradeDeal.builder()
				.id(1l).dealRef(UUID.randomUUID().toString())
				.timestamp(LocalDateTime.now())
				.baseCurrency("GBP").counterCurrency("USD")
				.rate(Math.random()).baseCurrencyAmount(BigDecimal.valueOf(1000)).customerId(1l)
				.tradeAction(TradeAction.BUY)
				.build();

		ForexTradeDeal deal3 = 
				ForexTradeDeal.builder()
				.id(1l).dealRef(UUID.randomUUID().toString())
				.timestamp(LocalDateTime.now())
				.baseCurrency("GBP").counterCurrency("USD")
				.rate(Math.random()).baseCurrencyAmount(BigDecimal.valueOf(1000)).customerId(1l)
				.tradeAction(TradeAction.BUY)
				.build();
				
		when(tradeService.retrieveTradeDealByCustomer(anyLong(), any()))
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
