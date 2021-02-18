package space.gavinklfong.forex.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import space.gavinklfong.forex.repos.RateBookingRepo;

@RestController
@RequestMapping("/rates")
public class RateRestController {

	private static Logger logger = LoggerFactory.getLogger(RateRestController.class);
	
	@Autowired
	private RateBookingRepo repos;
	
	@GetMapping(path = "latest", produces = "application/json")
	public Mono<String> getLatestRates() {
		WebClient client = WebClient.create("https://api.exchangeratesapi.io");
		
		Mono<String> response =  
				client.get().uri("/latest?base={base}", "GBP")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String.class);
		
		return response;
	}
		
}
