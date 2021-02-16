package space.gavinklfong.forex.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.TradeDealConfirmation;
import space.gavinklfong.forex.dto.TradeDealRequest;

@RestController
@RequestMapping("/forex/trade")
public class TradeDealRestController {

	@PostMapping(produces = "application/json")
	public Mono<TradeDealConfirmation> submitDeal(TradeDealRequest request) {
		
		return Mono.justOrEmpty(null);
	}
}
