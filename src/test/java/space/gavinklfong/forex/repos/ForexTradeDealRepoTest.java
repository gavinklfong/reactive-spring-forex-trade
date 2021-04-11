package space.gavinklfong.forex.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import space.gavinklfong.forex.models.ForexTradeDeal;


@Tag("UnitTest")
public class ForexTradeDealRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(ForexTradeDealRepoTest.class);	
	
	@Autowired
	private ForexTradeDealRepo tradeDealRepo;
	
	/**
	 * Verify the behaviour for customer without trade deal record
	 * 
	 */
	@Test
	void findByCustomerId_withNoRecords() {

		tradeDealRepo.findByCustomerId(2l)
		.as(StepVerifier::create)
		.verifyComplete();
		
	}	
	
	
	/**
	 * Verify the behaviour for customer with trade deal records
	 * 
	 */
	@Test
	void findByCustomerId_withRecords() {

		tradeDealRepo.findByCustomerId(1l)
		.as(StepVerifier::create)
		.expectNextMatches(item -> assertTradeDeal(item))
		.expectComplete()
		.verify();		
		
//		assertNotNull(results);
//		assertEquals(3, results.size());
//		
//		results.forEach(record ->  {
//			assertTradeDeal(record);
//		});
		
	}
	
	boolean assertTradeDeal(ForexTradeDeal record) {
		
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
		
		return true;
	}
	
}
