package space.gavinklfong.forex.repos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;


@DataJpaTest
public class RateBookingRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(RateBookingRepoTest.class);	
	
	@Autowired
	private RateBookingRepo rateBookingRepo;
	
	@DisplayName("Save rate booking")
	@Test
	void testSave() {
		
		UUID uuid = UUID.randomUUID();
		
		RateBooking rate = new RateBooking();
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
		
		
		RateBooking bookingOriginal = new RateBooking();
		bookingOriginal.setBaseCurrency("GBP");
		bookingOriginal.setCounterCurrency("USD");
		bookingOriginal.setTimestamp(LocalDateTime.now());
		bookingOriginal.setRate(Double.valueOf(2.25));
		bookingOriginal.setExpiryTime(LocalDateTime.now().plusMinutes(10));
		bookingOriginal.setBookingRef(uuid.toString());
		
		bookingOriginal = rateBookingRepo.save(bookingOriginal);
		
		Iterable<RateBooking> bookings = rateBookingRepo.findAll();
		
		int count = 0;

		Iterator<RateBooking> it = bookings.iterator();
		while (it.hasNext()) {
			count++;
			it.next();
		}
		
		assertTrue(count > 0);
	}	
	
//	@DisplayName("find by customer id")
//	@Test
//	void testFindByCustomerId() {
//
//		UUID uuid = UUID.randomUUID();		
//		
//		RateBooking rate = new RateBooking();
//		rate.setBaseCurrency("GBP");
//		rate.setCounterCurrency("USD");
//		rate.setTimestamp(LocalDateTime.now());
//		rate.setRate(Double.valueOf(2.25));
//		rate.setExpiryTime(LocalDateTime.now().plusMinutes(10));
//		rate.setBookingRef(uuid.toString());
//		
//		Customer cust = new Customer();
//		cust.setId(2l);
//		cust.setName("tester 2");
//		cust.setTier(1);
//		
//		rate.setCustomer(cust);
//		
//		rateBookingRepo.save(rate);
//		
//		List<RateBooking> bookings = rateBookingRepo.fin(1l);
//		
//		bookings.forEach(booking -> {
//			System.out.println(booking.getId() + ", name = " + booking.getCustomer().getName());
//			System.out.println(booking.getBookingRef());
//		});
//	}
	
}
