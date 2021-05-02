package space.gavinklfong.forex.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.dto.ForexRate;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.services.ForexPricingService;
import space.gavinklfong.forex.services.ForexRateService;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/rates")
public class ForexRateRestController {

	@Value("${app.default-base-currency}")
	private String defaultBaseCurrency;
		
	@Autowired
	private ForexRateService rateService;
		
	@Autowired
	private ForexPricingService pricingService;
	
	@GetMapping(path = {"base-currencies", "base-currencies/"})
	public Flux<String> getBaseCurrencies() {
		
		return Flux.fromStream(pricingService.findAllBaseCurrencies().stream());
		
	}
	
	@GetMapping(path = {"latest", "latest/", "latest/{optionalBaseCurrency}"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<ForexRate> getLatestRates(@PathVariable Optional<String> optionalBaseCurrency) throws InvalidRequestException {
		
		String baseCurrency = optionalBaseCurrency.orElse(defaultBaseCurrency);
				
		return rateService.fetchLatestRates(baseCurrency);
		
	}

	@GetMapping(path = "latest/{baseCurrency}/{counterCurrency}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ForexRate> getLatestRates(@PathVariable String baseCurrency, @PathVariable String counterCurrency) throws InvalidRequestException {
		
		return rateService.fetchLatestRate(baseCurrency, counterCurrency);
		
	}	
	
	@PostMapping(path = "book", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ForexRateBooking> bookRate(@Valid @RequestBody ForexRateBookingReq req) throws InvalidRequestException {
				
		// obtain booking
		return rateService.obtainBooking(req);
		
	}
		
}
