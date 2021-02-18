package space.gavinklfong.forex.services;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexRateApiResp;
import space.gavinklfong.forex.dto.Rate;
import space.gavinklfong.forex.dto.RateBookingReq;
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
	
	@BeforeEach
	public void setup() {
		Map<String, Double> rates = new HashMap<>();
		rates.put("USD", Double.valueOf(1.3868445262));
//		rates.put("EUR", Double.valueOf(1.1499540018));
		ForexRateApiResp forexRateApiResp = new ForexRateApiResp("GBP", LocalDate.now(), rates);
		
		when(forexRateApiClient.fetchLatestRates("GBP", "USD"))
		.thenReturn(Mono.just(forexRateApiResp));
		
		when(forexRateApiClient.fetchLatestRates("GBP"))
		.thenReturn(Mono.just(forexRateApiResp));
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.of(new Customer(1l, "Tester 1", 1)));
		
		when(rateBookingRepo.save(any(RateBooking.class)))
		.thenAnswer(invocation -> 
		invocation.getArgument(0)
//		((RateBooking)invocation.getArgument(0)).setId(1l)
		);
		
	}
	
	@Test
	public void fetchLatestRatesTest() {
		
//		Map<String, Double> rates = new HashMap<>();
//		rates.put("USD", Double.valueOf(1.3868445262));
//		rates.put("EUR", Double.valueOf(1.1499540018));
//		ForexRateApiResp forexRateApiResp = new ForexRateApiResp("GBP", LocalDate.now(), rates);
//		
//		when(forexRateApiClient.fetchLatestRates("GBP"))
//		.thenReturn(Mono.just(forexRateApiResp));
		
		Flux<Rate> resp = rateService.fetchLatestRates("GBP");
		
		resp.subscribe(rate -> {
			logger.info("currency = " + rate.getCounterCurrency() + ", rate = " + rate.getRate());
		});
		
	}
	
	@Test
	public void obtainBookingTest() {
		
		Map<String, Double> rates = new HashMap<>();
		rates.put("USD", Double.valueOf(1.3868445262));
//		rates.put("EUR", Double.valueOf(1.1499540018));
		ForexRateApiResp forexRateApiResp = new ForexRateApiResp("GBP", LocalDate.now(), rates);
		
		when(forexRateApiClient.fetchLatestRates("GBP", "USD"))
		.thenReturn(Mono.just(forexRateApiResp));
		
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.of(new Customer(1l, "Tester 1", 1)));
		
		when(rateBookingRepo.save(any(RateBooking.class)))
		.thenAnswer(invocation -> 
		invocation.getArgument(0)
//		((RateBooking)invocation.getArgument(0)).setId(1l)
		);

		
		Mono<ForexRateApiResp> resp = forexRateApiClient.fetchLatestRates("GBP", "USD");
		assertNotNull(resp);
		
		
//		RateBookingReq req = new RateBookingReq("GBP", "USD", BigDecimal.valueOf(1000), 1l);
//	
//		 Mono<RateBooking> rateBooking = rateService.obtainBooking(req);
//		 
//		 assertNotNull(rateBooking);
	}
	
}
