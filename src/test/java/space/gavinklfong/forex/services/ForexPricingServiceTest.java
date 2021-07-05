package space.gavinklfong.forex.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.slf4j.Slf4j;
import space.gavinklfong.forex.config.AppConfig;
import space.gavinklfong.forex.dto.ForexPricing;

@Slf4j
@SpringJUnitConfig
@TestPropertySource(properties = {
	    "app.rate-booking-duration=120",
	    "app.default-additional-pip=1"
})
@ContextConfiguration(classes = {AppConfig.class})
@Tag("UnitTest")
class ForexPricingServiceTest {
		    
    @Autowired
    ForexPricingService forexPriceService;
		
    private final static String[] BASE_CURRENCIES = {"AUD", "CAD", "CHF", "EUR", "GBP", "NZD", "USD"};
	
	@Test
	void findAllBaseCurrencies() throws CsvValidationException, IOException {			
		
		List<String> baseCurrencies = forexPriceService.findAllBaseCurrencies();
						
		baseCurrencies.forEach(c -> {
			log.info("===> " + c);
			assertTrue(Arrays.binarySearch(BASE_CURRENCIES, c) > -1); 
		});
		
		assertEquals(BASE_CURRENCIES.length, baseCurrencies.size());		
	}
	
	@Test
	void findCounterCurrencies() {
		
		List<ForexPricing> prices = forexPriceService.findCounterCurrencies("GBP");
		
		assertEquals(6, prices.size());
		
	}
	
}
