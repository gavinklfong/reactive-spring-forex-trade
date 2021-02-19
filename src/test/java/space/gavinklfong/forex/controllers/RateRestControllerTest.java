package space.gavinklfong.forex.controllers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
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
import space.gavinklfong.forex.dto.Rate;
import space.gavinklfong.forex.services.RateService;

@WebFluxTest(controllers = {RateRestController.class})
public class RateRestControllerTest {

	@MockBean
	private RateService rateService;
	
	@Autowired
	WebTestClient webTestClient;
	
	
	@Test
	public void getLatestRates() throws Exception {

		when(rateService.fetchLatestRates(anyString()))		
		.thenAnswer(invocation -> {
			String baseCurrency = (String) invocation.getArgument(0);
			LocalDateTime timestamp = LocalDateTime.now();
			return Flux.just(
					new Rate(timestamp, baseCurrency, "USD", Math.random()),
					new Rate(timestamp, baseCurrency, "EUR", Math.random()),
					new Rate(timestamp, baseCurrency, "CAD", Math.random()),
					new Rate(timestamp, baseCurrency, "JPY", Math.random()),
					new Rate(timestamp, baseCurrency, "GBP", 1d)					
					);
		});
		
		
		
		webTestClient.get()
		.uri("/rates/latest/GBP")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk();
//		.expectBody(List.class)
		
	}
	
	
}
