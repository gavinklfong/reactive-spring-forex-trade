package space.gavinklfong.forex.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.models.Rate;
import space.gavinklfong.forex.models.TradeOrderRequest;
import space.gavinklfong.forex.repos.ForexRepos;

@RestController
@RequestMapping("/")
public class ForexRestController {

	private static Logger logger = LoggerFactory.getLogger(ForexRestController.class);
	
	@Autowired
	private ForexRepos repos;
	
	@GetMapping(path = "rates/latest", produces = "application/json")
	public Mono<String> getLatestRates() {
		WebClient client = WebClient.create("https://api.exchangeratesapi.io");
		
		Mono<String> response =  
				client.get().uri("/latest?base={base}", "GBP")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String.class);
		
		return response;
	}
		
	@PostMapping(path = "orders", consumes = "application/json")
	public Mono<ResponseEntity> postOrder(@RequestBody TradeOrderRequest request) {
		
		
		return Mono.just(ResponseEntity.ok().build());
	}
	
	@GetMapping("rates/cache")
	public Flux<Rate> test() {
		
		Iterable<Rate> result = repos.findAll();		
		Flux<Rate> fluxResult = Flux.fromIterable(result);
		return fluxResult;
		
//		return repos.findAll();
		
	}
}
