package space.gavinklfong.forex.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexRate;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.dto.TradeAction;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.models.ForexTradeDeal;
import space.gavinklfong.forex.services.ForexPricingService;
import space.gavinklfong.forex.services.ForexRateService;

/**
 * Unit test for Rate Rest Controller
 *
 */
@WebFluxTest(controllers = {ForexRateRestController.class})
@Tag("UnitTest")
class ForexRateRestControllerTest {

	@MockBean
	private ForexRateService rateService;
	
	@MockBean
	private ForexPricingService pricingService;
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	void findAllBaseCurrecies() {
		when(pricingService.findAllBaseCurrencies())
		.thenReturn(List.of("AUD", "CAD", "CHF", "EUR", "GBP", "NZD", "USD"));
		
		webTestClient.get()
		.uri("/rates/base-currencies")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(resp -> assertThat(resp.getResponseBody()).isNotNull());
	}
	
	@Test
	void getLatestRates() throws Exception {

		// Mock return data of rate service
		when(rateService.fetchLatestRates(anyString()))		
		.thenAnswer(invocation -> {
			String baseCurrency = (String) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Flux.just(
					ForexRate.builder().timestamp(timestamp).baseCurrency(baseCurrency).counterCurrency("USD").buyRate(Math.random()).sellRate(Math.random()).build(),
					ForexRate.builder().timestamp(timestamp).baseCurrency(baseCurrency).counterCurrency("EUR").buyRate(Math.random()).sellRate(Math.random()).build(),
					ForexRate.builder().timestamp(timestamp).baseCurrency(baseCurrency).counterCurrency("CAD").buyRate(Math.random()).sellRate(Math.random()).build(),
					ForexRate.builder().timestamp(timestamp).baseCurrency(baseCurrency).counterCurrency("JPY").buyRate(Math.random()).sellRate(Math.random()).build()
			);
		});
		
		// trigger API request to rate controller
		webTestClient.get()
		.uri("/rates/latest/GBP")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$").isArray()
		.jsonPath("$[0].baseCurrency").isEqualTo("GBP")
		.jsonPath("$[0].counterCurrency").isEqualTo("USD")
		.jsonPath("$[0].buyRate").isNumber()
		.jsonPath("$[0].sellRate").isNumber()
		.jsonPath("$[1].baseCurrency").isEqualTo("GBP")
		.jsonPath("$[1].counterCurrency").isEqualTo("EUR")
		.jsonPath("$[1].buyRate").isNumber()
		.jsonPath("$[1].sellRate").isNumber()
		.jsonPath("$[2].baseCurrency").isEqualTo("GBP")
		.jsonPath("$[2].counterCurrency").isEqualTo("CAD")
		.jsonPath("$[2].buyRate").isNumber()
		.jsonPath("$[2].sellRate").isNumber()
		.jsonPath("$[3].baseCurrency").isEqualTo("GBP")
		.jsonPath("$[3].counterCurrency").isEqualTo("JPY")
		.jsonPath("$[3].buyRate").isNumber()
		.jsonPath("$[3].sellRate").isNumber();	
	}
	
	
	@Test
	void bookRate() throws InvalidRequestException {
		
		when(rateService.obtainBooking((any(ForexRateBookingReq.class))))
		.thenAnswer(invocation -> {
			ForexRateBookingReq req = (ForexRateBookingReq) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			LocalDateTime expiryTime = timestamp.plusMinutes(10);
			return Mono.just(
					new ForexRateBooking(1l, timestamp, req.getBaseCurrency(), req.getCounterCurrency(), 
							Math.random(), UUID.randomUUID().toString(), expiryTime, 1l)
					);
		});
		
		ForexRateBookingReq req = ForexRateBookingReq.builder()
				.baseCurrency("GBP").counterCurrency("USD")
				.baseCurrencyAmount(BigDecimal.valueOf(10000.25))
				.tradeAction(TradeAction.BUY).customerId(1l).build();
		
		webTestClient.post()
		.uri("/rates/book")
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), ForexTradeDealReq.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody(ForexRateBooking.class);		
		
//		webTestClient.get()
//		.uri(uriBuilder -> uriBuilder
//				.path("/rates/book")
//				.queryParam("baseCurrency", "GBP")
//				.queryParam("counterCurrency", "USD")
//				.queryParam("baseCurrencyAmount", 1000)
//				.queryParam("tradeAction", "BUY")
//				.queryParam("customerId", 1)
//				.build()
//				)
//		.exchange()
//		.expectStatus().isOk()
//		.expectBody(ForexRateBooking.class);
		
	}
	
	
	@Test
	public void bookRate_missingParam() throws InvalidRequestException {
		
		when(rateService.obtainBooking((any(ForexRateBookingReq.class))))
		.thenAnswer(invocation -> {
			ForexRateBookingReq req = (ForexRateBookingReq) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			LocalDateTime expiryTime = timestamp.plusMinutes(10);
			return Mono.just(
					new ForexRateBooking(1l, timestamp, req.getBaseCurrency(), req.getCounterCurrency(), 
							Math.random(), UUID.randomUUID().toString(), expiryTime, 1l)
					);
		});
		
		webTestClient.get()
		.uri(uriBuilder -> uriBuilder
				.path("/rates/book")
				.queryParam("counterCurrency", "USD")
				.queryParam("baseCurrencyAmount", 1000)
				.queryParam("customerId", 1)
				.build()
				)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorBody.class);
		
	}
	
	@Test
	public void bookRate_unknownCustomer() throws InvalidRequestException {
		
		when(rateService.obtainBooking((any(ForexRateBookingReq.class))))
		.thenThrow(new UnknownCustomerException());
		
		webTestClient.get()
		.uri(uriBuilder -> uriBuilder
				.path("/rates/book")
				.queryParam("counterCurrency", "USD")
				.queryParam("baseCurrencyAmount", 1000)
				.queryParam("customerId", 1)
				.build()
				)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorBody.class);
	}
	
}
