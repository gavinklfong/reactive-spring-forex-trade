package space.gavinklfong.forex.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import space.gavinklfong.forex.models.Rate;


@DataJpaTest
public class ForexReposTest {

    private static final Logger logger = LoggerFactory.getLogger(ForexReposTest.class);	
	
	@Autowired
	private ForexRepos forexRepo;
	
	@DisplayName("Save forex rates")
	@Test
	void testSave() {
		
		Rate rate = new Rate();
		rate.setBaseCurrency("GBP");
		rate.setCurrency("USD");
		rate.setDate(LocalDate.now());
		rate.setRate(2.25);
		rate.setExpiredTime(LocalDateTime.now().plusMinutes(10));
		rate.setReservationCode("ABC");
		
		forexRepo.save(rate);
		
		
//		assertEquals(1, recordSize);
//		assertEquals("Hong Kong", result.getCity());
//		assertEquals(30.5, result.getTemperature());
		
	}
	
	
}
