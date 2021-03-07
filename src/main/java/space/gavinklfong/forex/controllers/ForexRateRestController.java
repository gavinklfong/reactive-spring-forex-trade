package space.gavinklfong.forex.controllers;

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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.services.ForexRateService;
import space.gavinklfong.forex.dto.ForexRate;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.exceptions.InvalidRequestException;

@RestController
@RequestMapping("/rates")
public class ForexRateRestController {

	private static Logger logger = LoggerFactory.getLogger(ForexRateRestController.class);
	
	@Value("${app.default-base-currency")
	private String defaultBaseCurrency;
		
	@Autowired
	private ForexRateService rateService;
		
	
	@GetMapping(path = "latest/{baseCurrency}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<ForexRate> getLatestRates(@PathVariable String baseCurrency) {

		if (baseCurrency == null || baseCurrency.trim().length() == 0) {
			baseCurrency = defaultBaseCurrency;
		}
		
		return rateService.fetchLatestRates(baseCurrency);
		
	}
	
	@GetMapping(path = "book", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ForexRateBooking> bookRate(@Valid ForexRateBookingReq req, BindingResult bindingResult) throws InvalidRequestException {
		
		// validate request
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			return Mono.error(new InvalidRequestException(errors));
		}
		
		// obtain booking
		return rateService.obtainBooking(req);
		
	}
		
}
