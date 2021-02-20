package space.gavinklfong.forex.controllers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.Rate;
import space.gavinklfong.forex.services.RateService;
import space.gavinklfong.forex.dto.RateBookingReq;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;

@WebFluxTest(controllers = {RateRestController.class})
public class RateRestControllerTest {

	@MockBean
	private RateService rateService;
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	public void getLatestRates() throws Exception {

		when(rateService.fetchLatestRates(anyString()))		
		.thenAnswer(invocation -> {
			String baseCurrency = (String) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Flux.just(
					new Rate(timestamp, baseCurrency, "USD", Math.random()),
					new Rate(timestamp, baseCurrency, "EUR", Math.random()),
					new Rate(timestamp, baseCurrency, "CAD", Math.random()),
					new Rate(timestamp, baseCurrency, "JPY", Math.random())
					);
		});
		
		
		
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
	public void bookRate() throws UnknownCustomerException {
		
		when(rateService.obtainBooking((any(RateBookingReq.class))))
		.thenAnswer(invocation -> {
			RateBookingReq req = (RateBookingReq) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			LocalDateTime expiryTime = timestamp.plusMinutes(10);
			Customer customer = new Customer(req.getCustomerId(), "Tester 1", 1);
			return Mono.just(
					new RateBooking(1l, timestamp, req.getBaseCurrency(), req.getCounterCurrency(), 
							Math.random(), UUID.randomUUID().toString(), expiryTime, customer)
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
		.expectBody(RateBooking.class);
		
	}
	
	
	@Test
	public void bookRate_missingParam() throws UnknownCustomerException {
		
		when(rateService.obtainBooking((any(RateBookingReq.class))))
		.thenAnswer(invocation -> {
			RateBookingReq req = (RateBookingReq) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			LocalDateTime expiryTime = timestamp.plusMinutes(10);
			Customer customer = new Customer(req.getCustomerId(), "Tester 1", 1);
			return Mono.just(
					new RateBooking(1l, timestamp, req.getBaseCurrency(), req.getCounterCurrency(), 
							Math.random(), UUID.randomUUID().toString(), expiryTime, customer)
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
		
		when(rateService.obtainBooking((any(RateBookingReq.class))))
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
