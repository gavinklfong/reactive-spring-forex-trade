package space.gavinklfong.forex.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.opencsv.exceptions.CsvValidationException;

import space.gavinklfong.forex.config.AppConfig;

@SpringJUnitConfig
@TestPropertySource(properties = {
	    "app.rate-booking-duration=120",
	    "app.default-additional-pip=1"
})
@ContextConfiguration(classes = {AppConfig.class})
@Tag("UnitTest")
class ForexPriceServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(ForexPriceServiceTest.class);
	    
    @Autowired
    ForexPriceService forexPriceService;
		
	
	@Test
	void initialize() throws CsvValidationException, IOException {
		
		assertTrue(forexPriceService.findAdditionalPip("USD", "CAD") > 0);				

	}
	
}
