package space.gavinklfong.forex.controllers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.UUID;

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
import space.gavinklfong.forex.services.ForexRateService;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.ForexRateBooking;

/**
 * Unit test for Rate Rest Controller
 *
 */
@WebFluxTest(controllers = {ForexRateRestController.class})
@Tag("UnitTest")
class ForexRateRestControllerTest {

	@MockBean
	private ForexRateService rateService;
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	void getLatestRates() throws Exception {

		// Mock return data of rate service
		when(rateService.fetchLatestRates(anyString()))		
		.thenAnswer(invocation -> {
			String baseCurrency = (String) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Flux.just(
					new ForexRate(timestamp, baseCurrency, "USD", Math.random()),
					new ForexRate(timestamp, baseCurrency, "EUR", Math.random()),
					new ForexRate(timestamp, baseCurrency, "CAD", Math.random()),
					new ForexRate(timestamp, baseCurrency, "JPY", Math.random())
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
		.jsonPath("$[0].rate").isNumber()
		.jsonPath("$[1].baseCurrency").isEqualTo("GBP")
		.jsonPath("$[1].counterCurrency").isEqualTo("EUR")
		.jsonPath("$[1].rate").isNumber()
		.jsonPath("$[2].baseCurrency").isEqualTo("GBP")
		.jsonPath("$[2].counterCurrency").isEqualTo("CAD")
		.jsonPath("$[2].rate").isNumber()
		.jsonPath("$[3].baseCurrency").isEqualTo("GBP")
		.jsonPath("$[3].counterCurrency").isEqualTo("JPY")
		.jsonPath("$[3].rate").isNumber();		
	}
	
	
	@Test
	void bookRate() throws UnknownCustomerException {
		
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
				.queryParam("baseCurrency", "GBP")
				.queryParam("counterCurrency", "USD")
				.queryParam("baseCurrencyAmount", 1000)
				.queryParam("customerId", 1)
				.build()
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody(ForexRateBooking.class);
		
	}
	
	
	@Test
	public void bookRate_missingParam() throws UnknownCustomerException {
		
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
	public void bookRate_unknownCustomer() throws UnknownCustomerException {
		
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
