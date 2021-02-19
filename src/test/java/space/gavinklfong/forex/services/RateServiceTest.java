package space.gavinklfong.forex.services;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import antlr.collections.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
public class RateServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(RateServiceTest.class);
	
//	@Mock
//	private ForexRateApiClient forexRateApiClient;
//	@InjectMocks
//	private RateService rateService = new RateService();

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
		
		RateBooking mockRecord = new RateBooking("GBP", "USD", 0.25, "ABC");
		mockRecord.setExpiryTime(LocalDateTime.now().plusMinutes(15));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Arrays.asList(mockRecord));
		
		
		RateBooking rateBooking = new RateBooking("GBP", "USD", 0.25, "ABC");
		
		Boolean result = rateService.validateRateBooking(rateBooking).block();
		
		assertTrue(result);
		
	}
	
	@Test
	public void validateRateBookingTest_invalidBooking_notFound() {
		
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(null);
		
		
		RateBooking rateBooking = new RateBooking("GBP", "USD", 0.25, "ABC");
		
		Boolean result = rateService.validateRateBooking(rateBooking).block();
		
		assertFalse(result);
		
	}
	
	@Test
	public void validateRateBookingTest_invalidBooking_expired() {
		
		RateBooking mockRecord = new RateBooking("GBP", "USD", 0.25, "ABC");
		mockRecord.setExpiryTime(LocalDateTime.now().minusMinutes(15));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Arrays.asList(mockRecord));
		
		RateBooking rateBooking = new RateBooking("GBP", "USD", 0.25, "ABC");
		
		Boolean result = rateService.validateRateBooking(rateBooking).block();
		
		assertFalse(result);
		
	}
	
	
	@Test
	public void fetchLatestRatesTest() {
		
		Map<String, Double> rates = new HashMap<>();
		rates.put("USD", Double.valueOf(1.3868445262));
		rates.put("EUR", Double.valueOf(1.1499540018));
		ForexRateApiResp forexRateApiResp = new ForexRateApiResp("GBP", LocalDate.now(), rates);
		
		when(forexRateApiClient.fetchLatestRates("GBP"))
		.thenReturn(Mono.just(forexRateApiResp));
		
		Flux<Rate> resp = rateService.fetchLatestRates("GBP");
		
		resp.subscribe(rate -> {
			logger.info("currency = " + rate.getCounterCurrency() + ", rate = " + rate.getRate());
		});
		
	}
	
	@Test
	public void obtainBookingTest_CustomerTier1() throws JsonProcessingException, UnknownCustomerException {
		
		RateBooking rateBooking = obtainBookingTest(1);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(1.025, rateBooking.getRate());
		
		
		ObjectMapper mapper = new ObjectMapper();
		logger.info(mapper.writeValueAsString(rateBooking));
		
	}
	
	@Test
	public void obtainBookingTest_CustomerTier2() throws JsonProcessingException, UnknownCustomerException {
		
		RateBooking rateBooking = obtainBookingTest(2);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(1.05, rateBooking.getRate());
		
		
		ObjectMapper mapper = new ObjectMapper();
		logger.info(mapper.writeValueAsString(rateBooking));
		
	}
	
	@Test
	public void obtainBookingTest_CustomerTier3() throws JsonProcessingException, UnknownCustomerException {
		
		RateBooking rateBooking = obtainBookingTest(3);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(1.1, rateBooking.getRate());
		
		
		ObjectMapper mapper = new ObjectMapper();
		logger.info(mapper.writeValueAsString(rateBooking));
		
	}
	
	@Test
	public void obtainBookingTest_CustomerTier4() throws JsonProcessingException, UnknownCustomerException {
		
		RateBooking rateBooking = obtainBookingTest(4);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(1.5, rateBooking.getRate());
		
		
		ObjectMapper mapper = new ObjectMapper();
		logger.info(mapper.writeValueAsString(rateBooking));
		
	}
	
	private RateBooking obtainBookingTest(Integer tier) throws JsonProcessingException, UnknownCustomerException {
		
		
		when(forexRateApiClient.fetchLatestRates(anyString(), anyString()))
		.thenAnswer(invocation -> {
			Map<String, Double> rates = new HashMap<>();
			rates.put((String)invocation.getArgument(1), 1d);
			return Mono.just(new ForexRateApiResp((String)invocation.getArgument(0), LocalDate.now(), rates));
			
		});
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.of(new Customer(1l, "Tester 1", tier)));
		
		when(rateBookingRepo.save(any(RateBooking.class)))
		.thenAnswer(invocation -> {
			RateBooking record = (RateBooking)invocation.getArgument(0);
			record.setId((long)Math.random() * 10 + 1);
			return record;
		});

				
		RateBookingReq request = new RateBookingReq("GBP", "USD", BigDecimal.valueOf(1000), 1l);
		Mono<RateBooking> rateBookingMono = rateService.obtainBooking(request);
		return rateBookingMono.block();

		
	}
}
