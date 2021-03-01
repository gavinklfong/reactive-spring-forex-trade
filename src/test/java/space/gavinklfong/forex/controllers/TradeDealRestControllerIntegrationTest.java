package space.gavinklfong.forex.controllers;

import static org.mockserver.mock.OpenAPIExpectation.openAPIExpectation;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.TradeDealReq;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.models.TradeDeal;
import space.gavinklfong.forex.services.ForexRateApiClient;
import space.gavinklfong.forex.services.RateService;

@MockServerTest("server.url=http://localhost:${mockServerPort}")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
@Tag("IntegrationTest")
public class TradeDealRestControllerIntegrationTest {
	
	// Configure forex rate api client to point to mock api server
    @TestConfiguration
    static class TestContextConfiguration {
    	@Bean
    	@Primary
    	public ForexRateApiClient initializeForexRateApiClient(@Value("${server.url}") String url) {	
    		return new ForexRateApiClient(url);
    	}
    }
        
	private MockServerClient mockServerClient;
	
	@Autowired
	WebTestClient webTestClient;
	
	@DisplayName("submitDeal - Success case")
	@Test
	public void submitDeal() throws Exception {
		
		// Setup request matcher and response using OpenAPI definition
		mockServerClient
	    .upsert(
	        openAPIExpectation("mockapi/getLatestUSDRate.json")
	        .withOperationsAndResponses(Collections.singletonMap("getLatestRates", "200"))  
	    );
		
		// Fire request to obtain rate booking
		EntityExchangeResult<RateBooking> result = webTestClient.get()
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
		.expectBody(RateBooking.class)
		.returnResult();
		
		RateBooking rateBooking = result.getResponseBody();
		
		// construct and trigger trade deal request
		TradeDealReq req = new TradeDealReq("GBP", "USD", rateBooking.getRate(), BigDecimal.valueOf(1000),
				 1l,  rateBooking.getBookingRef());
		
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

		// send an empty request
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
