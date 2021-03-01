package space.gavinklfong.forex.services;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import space.gavinklfong.forex.dto.ForexRateApiResp;
import space.gavinklfong.forex.dto.Rate;
import space.gavinklfong.forex.dto.RateBookingReq;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.RateBookingRepo;

@SpringJUnitConfig
@TestPropertySource(properties = {
	    "app.rate-booking-duration=120",
})
@ContextConfiguration(classes = {RateService.class})
@Tag("UnitTest")
public class RateServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(RateServiceTest.class);
	
	@MockBean
	private ForexRateApiClient forexRateApiClient;
	
	@MockBean
	private CustomerRepo customerRepo;
	
	@MockBean
	private RateBookingRepo rateBookingRepo;

	@Autowired
	private RateService rateService;
	
	@Test
	public void validateRateBookingTest_validBooking() {
		
		RateBooking mockRecord = new RateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		mockRecord.setExpiryTime(LocalDateTime.now().plusMinutes(15));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Arrays.asList(mockRecord));
		
		RateBooking rateBooking = new RateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
		StepVerifier.create(result)
		.expectNext(true)
		.verifyComplete();		
	}
	
	@Test
	public void validateRateBookingTest_invalidBooking_notFound() {
		
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(null);
		
		RateBooking rateBooking = new RateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
		StepVerifier.create(result)
		.expectNext(false)
		.verifyComplete();		
	}
	
	@Test
	public void validateRateBookingTest_invalidBooking_expired() {
		
		RateBooking mockRecord = new RateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		mockRecord.setExpiryTime(LocalDateTime.now().minusMinutes(15));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Arrays.asList(mockRecord));
		
		RateBooking rateBooking = new RateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
		StepVerifier.create(result)
		.expectNext(false)
		.verifyComplete();	
		
	}
	
	
	@Test
	public void fetchLatestRatesTest() {
		
		final double USD_RATE = 1.3868445262;
		final double EUR_RATE = 1.1499540018;
		
		Map<String, Double> rates = new HashMap<>();
		rates.put("USD", Double.valueOf(USD_RATE));
		rates.put("EUR", Double.valueOf(EUR_RATE));
		ForexRateApiResp forexRateApiResp = new ForexRateApiResp("GBP", LocalDate.now(), rates);
		
		when(forexRateApiClient.fetchLatestRates("GBP"))
		.thenReturn(Mono.just(forexRateApiResp));
		
		Flux<Rate> resp = rateService.fetchLatestRates("GBP");
		StepVerifier.create(resp)
		.expectNextMatches(rate -> rate.getBaseCurrency().contentEquals("GBP") && rate.getCounterCurrency().contentEquals("EUR") && rate.getRate() == EUR_RATE)
		.expectNextMatches(rate ->  rate.getBaseCurrency().contentEquals("GBP") && rate.getCounterCurrency().contentEquals("USD") && rate.getRate() == USD_RATE)
		.verifyComplete();	
		
	}
	
	@Test
	public void obtainBookingTest_CustomerTier1() throws JsonProcessingException, UnknownCustomerException {
		
		RateBooking rateBooking = obtainBookingTest(1);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(1 + RateTier.TIER1.rate, rateBooking.getRate());	
		
	}
	
	@Test
	public void obtainBookingTest_CustomerTier2() throws JsonProcessingException, UnknownCustomerException {
		
		RateBooking rateBooking = obtainBookingTest(2);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(1 + RateTier.TIER2.rate, rateBooking.getRate());	
	}
	
	@Test
	public void obtainBookingTest_CustomerTier3() throws JsonProcessingException, UnknownCustomerException {
		
		RateBooking rateBooking = obtainBookingTest(3);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(1 + RateTier.TIER3.rate, rateBooking.getRate());	
		
	}
	
	@Test
	public void obtainBookingTest_CustomerTier4() throws JsonProcessingException, UnknownCustomerException {
		
		// Test rate booking for customer with tier 4 account
		RateBooking rateBooking = obtainBookingTest(4);
		
		// Assert result
		assertNotNull(rateBooking);
		
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		
		assertEquals(1 + RateTier.TIER4.rate, rateBooking.getRate());	
	}
	
	
	private RateBooking obtainBookingTest(Integer tier) throws JsonProcessingException, UnknownCustomerException {
		
		// Forex API client returns 1 when fetchLatestRates() is invoked
		when(forexRateApiClient.fetchLatestRates(anyString(), anyString()))
		.thenAnswer(invocation -> {
			Map<String, Double> rates = new HashMap<>();
			rates.put((String)invocation.getArgument(1), 1d);
			return Mono.just(new ForexRateApiResp((String)invocation.getArgument(0), LocalDate.now(), rates));
		});
		
		// Customer Repo return a mock customer record when findById() is invoked
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.of(new Customer(1l, "Tester 1", tier)));
		
		// Rate Booking Repo return a mock return when save() is invoked
		when(rateBookingRepo.save(any(RateBooking.class)))
		.thenAnswer(invocation -> {
			RateBooking record = (RateBooking)invocation.getArgument(0);
			record.setId((long)Math.random() * 10 + 1);
			return record;
		});

		// Create a request to test obtainBooking()
		RateBookingReq request = new RateBookingReq("GBP", "USD", BigDecimal.valueOf(1000), 1l);
		Mono<RateBooking> rateBookingMono = rateService.obtainBooking(request);

		return rateBookingMono.block();
	}
}
