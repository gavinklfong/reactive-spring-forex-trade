package space.gavinklfong.forex.controllers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.TradeDealReq;
import space.gavinklfong.forex.services.RateService;
import space.gavinklfong.forex.services.TradeService;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.models.TradeDeal;

@WebFluxTest(controllers = {TradeDealRestController.class})
public class TradeDealRestControllerTest {

	@MockBean
	private TradeService tradeService;
	
	@Autowired
	WebTestClient webTestClient;
	
	@Test
	public void submitDeal() throws Exception {

		when(tradeService.postTradeDeal(any(TradeDealReq.class)))
		.thenAnswer(invocation -> {
			TradeDealReq req = (TradeDealReq)invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Mono.just(new TradeDeal(1l, UUID.randomUUID().toString(),  timestamp, req.getBaseCurrency(), req.getCounterCurrency(),
					 req.getRate(), req.getBaseCurrencyAmount(), new Customer(1l, "Tester 1", 1)));
		});
			
		TradeDealReq req = new TradeDealReq("GBP", "USD", 0.25, BigDecimal.valueOf(10000),
				 1l,  "ABC");
		
		webTestClient.post()
		.uri("/deals")
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), TradeDealReq.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody(TradeDeal.class);
	}
	
}
