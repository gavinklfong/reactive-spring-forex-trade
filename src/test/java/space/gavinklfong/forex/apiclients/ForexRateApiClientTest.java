package space.gavinklfong.forex.apiclients;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexRateApiResp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.mock.OpenAPIExpectation.openAPIExpectation;

/**
 * Unit test of forex rate API client.
 * 
 * Mock: external API is mocked using Mock API server which is initialized 
 * with OpenAPI definition. Example response is the mock response from external API
 * 
 * @author Gavin Fong
 *
 */
@MockServerTest("server.url=http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
@Tag("UnitTest")
public class ForexRateApiClientTest {
	
	private static Logger logger = LoggerFactory.getLogger(ForexRateApiClientTest.class);

    @Value("${server.url}")
    private String serverUrl;
	
    // Mock API server client is initialized and injected automatically
	private MockServerClient mockServerClient;
			
	@Test
	public void getLatestRate() throws URISyntaxException, IOException {
		
		// Setup request matcher and response using OpenAPI definition
		mockServerClient
	    .upsert(
	        openAPIExpectation("mockapi/getLatestRates.json")
	        .withOperationsAndResponses(Collections.singletonMap("getLatestRates", "200"))  
	    );
		
		// Initialize API client and trigger request
		ForexRateApiClient forexRateApiClient = new ForexRateApiClient(serverUrl);
		Mono<ForexRateApiResp> response = forexRateApiClient.fetchLatestRates("GBP");
		
		// Assert response
		ForexRateApiResp rawData = response.block();
		assertEquals(rawData.getBase(), "GBP");
		
		Map<String, Double> rates = rawData.getRates();
		assertEquals(4, rates.size());
		
		assertTrue(rates.containsKey("USD"));
		assertTrue(rates.containsKey("EUR"));
		assertTrue(rates.containsKey("CAD"));
		assertTrue(rates.containsKey("JPY"));

	}
	
	@Test
	public void getUSDRate() throws URISyntaxException, IOException {
		
		// Setup request matcher and response using OpenAPI definition
		mockServerClient
	    .upsert(
	        openAPIExpectation("mockapi/getLatestUSDRate.json")
	        .withOperationsAndResponses(Collections.singletonMap("getLatestRates", "200"))  
	    );
		
		// Initialize API client and trigger request
		ForexRateApiClient forexRateApiClient = new ForexRateApiClient(serverUrl);
		Mono<ForexRateApiResp> response = forexRateApiClient.fetchLatestRate("GBP", "USD");
		
		// Assert response
		ForexRateApiResp rawData = response.block();
		assertEquals(rawData.getBase(), "GBP");
		
		Map<String, Double> rates = rawData.getRates();
		assertEquals(1, rates.size());
		
		assertTrue(rates.containsKey("USD"));
		assertEquals(1.3923701653, rates.get("USD"));

	}
	
	
	
}
