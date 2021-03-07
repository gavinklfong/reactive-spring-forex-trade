package space.gavinklfong.forex.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.models.ForexTradeDeal;
import space.gavinklfong.forex.services.ForexTradeService;

@RestController
@RequestMapping("/deals")
public class ForexTradeDealRestController {
	
	private static Logger logger = LoggerFactory.getLogger(ForexTradeDealRestController.class);

	@Autowired
	private ForexTradeService tradeService;
	
	/**
	 * Expose API for customers to retrieve their forex trade deals
	 * 
	 * API - GET /deals
	 * 
	 * @param customerId. Exception will be thrown if customer id is empty, 
	 * the exception will be translated to HTTP response with 4xx status
	 * 
	 * @return List of forex trade deal records. The framework formats it into JSON format when sending HTTP response
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<ForexTradeDeal> getDeals(@RequestParam Long customerId) {
		
		if (customerId == null) {
			return Flux.error(new InvalidRequestException("customerId", "customer Id cannot be blank"));
		}
				
		return tradeService.retrieveTradeDealByCustomer(customerId);
	}
	
	/**
	 * Expose API for customers to post forex trade deal
	 * 
	 * API - POST /deals
	 * 
	 * @param req - Java bean contains trade deal information.
	 * '@RequestBody' annotation instructs the framework to convert JSON format into Java bean
	 * while '@Valid' annotation tells the framework to validate the object otherwise exception will be thrown
	 * and send HTTP response with 4xx status back to client
	 * 
	 * 
	 * @return Trade deal object. The framework formats it into JSON format when sending HTTP response
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ForexTradeDeal> submitDeal(@Valid @RequestBody Mono<ForexTradeDealReq> req) {
		
		// submit trade deal
		return req.flatMap(dealReq -> tradeService.postTradeDeal(dealReq));	
	}
	

}