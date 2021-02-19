package space.gavinklfong.forex.controllers;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.repos.RateBookingRepo;
import space.gavinklfong.forex.services.RateService;
import space.gavinklfong.forex.dto.Rate;
import space.gavinklfong.forex.dto.RateBookingReq;
import space.gavinklfong.forex.models.RateBooking;
import space.gavinklfong.forex.exceptions.InvalidRequestException;

@RestController
@RequestMapping("/rates")
public class RateRestController {

	private static Logger logger = LoggerFactory.getLogger(RateRestController.class);
	
	@Value("${app.default-base-currency")
	private String defaultBaseCurrency;
		
	@Autowired
	private RateService rateService;
		
	
	@GetMapping(path = "latest/{baseCurrency}", produces = "application/json")
	public Flux<Rate> getLatestRates(@PathVariable String baseCurrency) {

		if (baseCurrency == null || baseCurrency.trim().length() == 0) {
			baseCurrency = defaultBaseCurrency;
		}
		
		return rateService.fetchLatestRates(baseCurrency);
		
	}
	
	@GetMapping(path = "book", produces = "application/json")
	public Mono<RateBooking> bookRate(@Valid RateBookingReq req, BindingResult bindingResult) throws InvalidRequestException {
		
		// validate request
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			return Mono.error(new InvalidRequestException(errors));
		}
		
		// obtain booking
		return rateService.obtainBooking(req);
		
	}
		
}
