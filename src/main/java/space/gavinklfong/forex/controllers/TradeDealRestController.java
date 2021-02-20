package space.gavinklfong.forex.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.TradeDealReq;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.models.TradeDeal;
import space.gavinklfong.forex.services.TradeService;

@RestController
@RequestMapping("/deals")
public class TradeDealRestController {

	@Autowired
	private TradeService tradeService;
	
	
	@PostMapping(produces = "application/json")
	public Mono<TradeDeal> submitDeal(@Valid @RequestBody Mono<TradeDealReq> monoReq, BindingResult bindingResult) {
		
		// validate request
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			return Mono.error(new InvalidRequestException(errors));
		}
		
		// submit trade deal
		return monoReq.flatMap(req -> tradeService.postTradeDeal(req));	
	}
	
	@GetMapping(produces = "application/json")
	public Flux<TradeDeal> getDeals(@RequestParam Optional<Long> customerId, BindingResult bindingResult) {
		
		// validate request
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			return Flux.error(new InvalidRequestException(errors));
		}
		
		if (customerId.isEmpty()) {
			return Flux.error(new InvalidRequestException("customerId", "customer Id cannot be blank"));
		}
				
		return tradeService.retrieveTradeDealByCustomer(customerId.get());
	}
}
