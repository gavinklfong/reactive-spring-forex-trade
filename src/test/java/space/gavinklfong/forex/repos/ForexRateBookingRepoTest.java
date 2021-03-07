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
import org.springframework.test.context.jdbc.Sql;

import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.ForexRateBooking;


@DataJpaTest
@Sql({"/data-unittest.sql"})
@Tag("UnitTest")
public class ForexRateBookingRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(ForexRateBookingRepoTest.class);	
	
	@Autowired
	private ForexRateBookingRepo rateBookingRepo;
	
	@DisplayName("save rate booking")
	@Test
	void testSave() {
		
		UUID uuid = UUID.randomUUID();
		
		ForexRateBooking rate = new ForexRateBooking();
		rate.setBaseCurrency("GBP");
		rate.setCounterCurrency("USD");
		rate.setTimestamp(LocalDateTime.now());
		rate.setRate(Double.valueOf(2.25));
		rate.setExpiryTime(LocalDateTime.now().plusMinutes(10));
		rate.setBookingRef(uuid.toString());
		
		Customer cust = new Customer();
		cust.setName("tester");
		cust.setTier(1);
		
		rate.setCustomer(cust);
		
		
		rate = rateBookingRepo.save(rate);
		
		assertNotNull(rate.getId());
		
		
	}
	
	@DisplayName("find all rate booking")
	@Test
	void testFindAll() {
		
		UUID uuid = UUID.randomUUID();
		
		
		ForexRateBooking bookingOriginal = new ForexRateBooking();
		bookingOriginal.setBaseCurrency("GBP");
		bookingOriginal.setCounterCurrency("USD");
		bookingOriginal.setTimestamp(LocalDateTime.now());
		bookingOriginal.setRate(Double.valueOf(2.25));
		bookingOriginal.setExpiryTime(LocalDateTime.now().plusMinutes(10));
		bookingOriginal.setBookingRef(uuid.toString());
		
		bookingOriginal = rateBookingRepo.save(bookingOriginal);
		
		Iterable<ForexRateBooking> bookings = rateBookingRepo.findAll();
		
		int count = 0;

		Iterator<ForexRateBooking> it = bookings.iterator();
		while (it.hasNext()) {
			count++;
			it.next();
		}
		
		assertTrue(count > 0);
	}	
	
	@DisplayName("find by booking ref")
	@Test
	void findByBookingRef_withRecord() {

		List<ForexRateBooking> bookings = rateBookingRepo.findByBookingRef("BOOKING-REF-01");
		
		assertNotNull(bookings);
		assertEquals(1, bookings.size());
		
		ForexRateBooking booking = bookings.get(0);
		assertEquals("2021-02-01T11:50:00", booking.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		assertEquals("2021-02-01T12:10:00", booking.getExpiryTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		assertEquals("GBP", booking.getBaseCurrency());
		assertEquals("USD", booking.getCounterCurrency());
		assertEquals(1000d, booking.getBaseCurrencyAmount().doubleValue());	
	}
	
	@DisplayName("find by booking ref - no record")
	@Test
	void findByBookingRef_noRecord() {

		List<ForexRateBooking> bookings = rateBookingRepo.findByBookingRef("BOOKING-REF-02");
		
		assertNotNull(bookings);
		assertEquals(0, bookings.size());

	}
	
}
