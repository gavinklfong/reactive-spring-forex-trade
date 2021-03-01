package space.gavinklfong.forex.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.models.TradeDeal;


@DataJpaTest
@Sql({"/data-unittest.sql"})
@Tag("UnitTest")
public class TradeDealRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(TradeDealRepoTest.class);	
	
	@Autowired
	private TradeDealRepo tradeDealRepo;
	
	/**
	 * Verify the behaviour for customer without trade deal record
	 * 
	 */
	@Test
	void findByCustomerId_withNoRecords() {

		List<TradeDeal> results = tradeDealRepo.findByCustomerId(2l);
		
		assertNotNull(results);
		assertEquals(0, results.size());
				
	}	
	
	
	/**
	 * Verify the behaviour for customer with trade deal records
	 * 
	 */
	@Test
	void findByCustomerId_withRecords() {

		List<TradeDeal> results = tradeDealRepo.findByCustomerId(1l);
		
		assertNotNull(results);
		assertEquals(3, results.size());
		
		results.forEach(record ->  {
			assertTradeDeal(record);
		});
		
	}
	
	void assertTradeDeal(TradeDeal record) {
		
		switch (record.getDealRef()) {
			case "DEAL-REF-01":
				assertEquals("2021-02-01T12:00:00", record.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				assertEquals("GBP", record.getBaseCurrency());
				assertEquals("USD", record.getCounterCurrency());
				assertEquals(1.3690754045, record.getRate());
				assertEquals(1000d, record.getBaseCurrencyAmount().doubleValue());	
			break;
			case "DEAL-REF-02":
				assertEquals("2021-02-02T12:00:00", record.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				assertEquals("EUR", record.getBaseCurrency());
				assertEquals("CAD", record.getCounterCurrency());
				assertEquals(1.5331, record.getRate());
				assertEquals(2000d, record.getBaseCurrencyAmount().doubleValue());					
			break;
			case "DEAL-REF-03":
				assertEquals("2021-02-03T12:00:00", record.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				assertEquals("USD", record.getBaseCurrency());
				assertEquals("EUR", record.getCounterCurrency());
				assertEquals(0.8250144378, record.getRate());
				assertEquals(3000d, record.getBaseCurrencyAmount().doubleValue());	
			break;
			
		}
		
	}
	
}
