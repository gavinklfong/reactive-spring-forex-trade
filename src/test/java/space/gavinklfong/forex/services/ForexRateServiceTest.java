package space.gavinklfong.forex.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import space.gavinklfong.forex.apiclients.ForexRateApiClient;
import space.gavinklfong.forex.dto.ForexPricing;
import space.gavinklfong.forex.dto.ForexRate;
import space.gavinklfong.forex.dto.ForexRateApiResp;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.dto.TradeAction;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.ForexRateBookingRepo;

@SpringJUnitConfig
@TestPropertySource(properties = {
	    "app.rate-booking-duration=120",
	    "app.default-additional-pip=1"
})
@ContextConfiguration(classes = {ForexRateService.class})
@Tag("UnitTest")
public class ForexRateServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(ForexRateServiceTest.class);
	
	@MockBean
	private ForexRateApiClient forexRateApiClient;
	
	@MockBean
	private CustomerRepo customerRepo;
	
	@MockBean
	private ForexRateBookingRepo rateBookingRepo;
	
	@MockBean
	private ForexPricingService forexPriceService;

	@Autowired
	private ForexRateService rateService;
	
	private static final double ADDITIONAL_PIP = 0.0001;
	
	@Test
	void validateRateBookingTest_validBooking() {
		
		ForexRateBooking mockRecord = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		mockRecord.setExpiryTime(LocalDateTime.now().plusMinutes(15));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Mono.just(mockRecord));
		
		ForexRateBooking rateBooking = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
		StepVerifier.create(result)
		.expectNext(true)
		.verifyComplete();		
	}
	
	@Test
	void validateRateBookingTest_invalidBooking_notFound() {
		
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Mono.empty());
		
		ForexRateBooking rateBooking = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
		StepVerifier.create(result)
		.expectNext(false)
		.expectComplete();		
	}
	
	@Test
	void validateRateBookingTest_invalidBooking_expired() {
		
		ForexRateBooking mockRecord = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		mockRecord.setExpiryTime(LocalDateTime.now().minusMinutes(15));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Mono.just(mockRecord));
		
		ForexRateBooking rateBooking = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Mono<Boolean> result = rateService.validateRateBooking(rateBooking);
		StepVerifier.create(result)
		.expectNext(false)
		.verifyComplete();	
		
	}
	
	
	@Test
	void fetchLatestRatesTest() throws InvalidRequestException {
		
		final double USD_RATE = 1.3868445262;
		final double EUR_RATE = 1.1499540018;
		
		Map<String, Double> rates = new HashMap<>();
		rates.put("USD", Double.valueOf(USD_RATE));
		rates.put("EUR", Double.valueOf(EUR_RATE));
		ForexRateApiResp forexRateApiResp = new ForexRateApiResp("GBP", LocalDate.now(), rates);
		
		when(forexRateApiClient.fetchLatestRates("GBP"))
		.thenReturn(Mono.just(forexRateApiResp));
		
		when(forexPriceService.obtainForexPrice(anyString(), anyString(), anyDouble()))
		.thenAnswer(invocation -> {
			String base = (String)invocation.getArgument(0);
			String counter = (String)invocation.getArgument(1);
			Double rate = (Double)invocation.getArgument(2);
			return ForexRate.builder().
					baseCurrency(base).counterCurrency(counter)
					.buyRate(rate + 2).sellRate(rate + 4).build();
		});
		
		List<ForexPricing> forexPrices = Arrays.asList(
				ForexPricing.builder().baseCurrency("GBP").counterCurrency("EUR").sellPip(2).buyPip(4).build(),
				ForexPricing.builder().baseCurrency("GBP").counterCurrency("USD").sellPip(1).buyPip(2).build()
		);
		when(forexPriceService.findCounterCurrencies(anyString())).thenReturn(forexPrices);
		
		Flux<ForexRate> resp = rateService.fetchLatestRates("GBP");
		StepVerifier.create(resp)
		.expectNextMatches(rate -> rate.getBaseCurrency().contentEquals("GBP") 
				&& rate.getCounterCurrency().contentEquals("EUR") 
				&& rate.getBuyRate() > EUR_RATE
				&& rate.getBuyRate() < rate.getSellRate())		
		.expectNextMatches(rate ->  rate.getBaseCurrency().contentEquals("GBP") 
				&& rate.getCounterCurrency().contentEquals("USD") 
				&& rate.getBuyRate() > USD_RATE
				&& rate.getBuyRate() < rate.getSellRate())
		.verifyComplete();	
		
	}
	
	@Test
	void obtainBookingTest_CustomerTier1() throws JsonProcessingException, InvalidRequestException {
		
		ForexRateBooking rateBooking = obtainBookingTest(1);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(CustomerRateTier.TIER1.rate + 3, rateBooking.getRate());	
		
	}
	
	@Test
	void obtainBookingTest_CustomerTier2() throws JsonProcessingException, InvalidRequestException {
		
		ForexRateBooking rateBooking = obtainBookingTest(2);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(CustomerRateTier.TIER2.rate + 3, rateBooking.getRate());	
	}
	
	@Test
	void obtainBookingTest_CustomerTier3() throws JsonProcessingException, InvalidRequestException {
		
		ForexRateBooking rateBooking = obtainBookingTest(3);
		assertNotNull(rateBooking);
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(CustomerRateTier.TIER3.rate + 3, rateBooking.getRate());	
		
	}
	
	@Test
	void obtainBookingTest_CustomerTier4() throws JsonProcessingException, InvalidRequestException {
		
		// Test rate booking for customer with tier 4 account
		ForexRateBooking rateBooking = obtainBookingTest(4);
		
		// Assert result
		assertNotNull(rateBooking);
		
		LocalDateTime timestamp = rateBooking.getTimestamp();
		LocalDateTime expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		
		assertEquals( CustomerRateTier.TIER4.rate + 3, rateBooking.getRate());	
	}
	
	
	private ForexRateBooking obtainBookingTest(Integer tier) throws JsonProcessingException, InvalidRequestException {
		
		// Forex API client returns 1 when fetchLatestRates() is invoked
		when(forexRateApiClient.fetchLatestRate(anyString(), anyString()))
		.thenAnswer(invocation -> {
			Map<String, Double> rates = new HashMap<>();
			rates.put((String)invocation.getArgument(1), 1d);
			return Mono.just(new ForexRateApiResp((String)invocation.getArgument(0), LocalDate.now(), rates));
		});
		
		// Customer Repo return a mock customer record when findById() is invoked
		when(customerRepo.findById(anyLong()))
		.thenReturn(Mono.just(new Customer(1l, "Tester 1", tier)));
		
		// Rate Booking Repo return a mock return when save() is invoked
		when(rateBookingRepo.save(any(ForexRateBooking.class)))
		.thenAnswer(invocation -> {
			ForexRateBooking record = (ForexRateBooking)invocation.getArgument(0);
			record.setId((long)Math.random() * 10 + 1);
			return Mono.just(record);
		});
		
		when(forexPriceService.obtainForexPrice(anyString(), anyString(), anyDouble()))
		.thenAnswer(invocation -> {
			String base = (String)invocation.getArgument(0);
			String counter = (String)invocation.getArgument(1);
			Double rate = (Double)invocation.getArgument(2);
			return ForexRate.builder().
					baseCurrency(base).counterCurrency(counter)
					.buyRate(rate + 2).sellRate(rate + 4).build();
		});
		
		List<ForexPricing> forexPrices = Arrays.asList(
				ForexPricing.builder().baseCurrency("GBP").counterCurrency("EUR").sellPip(2).buyPip(4).build(),
				ForexPricing.builder().baseCurrency("GBP").counterCurrency("USD").sellPip(1).buyPip(2).build()
		);
		when(forexPriceService.findCounterCurrencies(anyString())).thenReturn(forexPrices);		
		
		// Create a request to test obtainBooking()
		ForexRateBookingReq request = ForexRateBookingReq.builder()
				.baseCurrency("GBP").counterCurrency("USD")
				.baseCurrencyAmount(BigDecimal.valueOf(1000))
				.customerId(1l).tradeAction(TradeAction.BUY)
				.build();
		Mono<ForexRateBooking> rateBookingMono = rateService.obtainBooking(request);

		return rateBookingMono.block();
	}
}
