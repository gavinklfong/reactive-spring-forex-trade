package space.gavinklfong.forex.repos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import space.gavinklfong.forex.models.RateBooking;


@DataJpaTest
public class RateBookingRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(RateBookingRepoTest.class);	
	
	@Autowired
	private RateBookingRepo rateBookingRepo;
	
	@DisplayName("Save rate booking")
	@Test
	void testSave() {
		
		RateBooking rate = new RateBooking();
		rate.setBaseCurrency("GBP");
		rate.setCounterCurrency("USD");
		rate.setTimestamp(LocalDateTime.now());
		rate.setRate(BigDecimal.valueOf(2.25));
		rate.setExpiryTime(LocalDateTime.now().plusMinutes(10));
		rate.setBookingRef("ABC1");
		
		rateBookingRepo.save(rate);
		
		
//		assertEquals(1, recordSize);
//		assertEquals("Hong Kong", result.getCity());
//		assertEquals(30.5, result.getTemperature());
		
	}
	
	
}
