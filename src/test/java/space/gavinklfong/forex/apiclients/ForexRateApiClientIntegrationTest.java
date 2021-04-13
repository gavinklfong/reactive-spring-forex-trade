package space.gavinklfong.forex.apiclients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import space.gavinklfong.forex.dto.ForexRateApiResp;

@Slf4j
@ExtendWith(SpringExtension.class)
@Tag("IntegrationTest")
public class ForexRateApiClientIntegrationTest {
	
    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public ForexRateApiClient rateClient() {
            return new ForexRateApiClient("http://localhost:3000");
        }
    }
	
	@Autowired
	private ForexRateApiClient client;
		
	@Test
	public void retrieveLatestRates() {
		
		final String BASE = "GBP";
		
		ForexRateApiResp resp = client.fetchLatestRates(BASE).block();
		
		assertNotNull(resp);
		assertNotNull(resp.getDate());
		assertEquals(BASE, resp.getBase());
		assertNotNull(resp.getRates());
		assertTrue(resp.getRates().size() > 1);
		assertNotNull(resp.getRates().get("USD"));
		assertNotNull(resp.getRates().get("EUR"));

	}
	
	@Test
	public void retrieveLatestRateByCounterCurrency() {
		
		final String BASE = "GBP";
		final String COUNTER = "USD";
		
		ForexRateApiResp resp = client.fetchLatestRate(BASE, COUNTER).block();
		
		assertNotNull(resp);
		assertNotNull(resp.getDate());
		assertEquals(BASE, resp.getBase());
		assertNotNull(resp.getRates());
		assertTrue(resp.getRates().size() == 1);
		assertNotNull(resp.getRates().get(COUNTER));
	}
	
}
