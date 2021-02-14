package space.gavinklfong.forex.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import space.gavinklfong.forex.models.Rate;

@Component
public class RateService {

	public Mono<Rate> fetchRate(String baseCurrency, String targetCurrency, BigDecimal baseCurrencyAmount ) {
		
		return Mono.just(new Rate());
	}
	
	public Mono<Boolean> validateRate(Rate rate) {
		
		return Mono.just(Boolean.valueOf(true));
	}
}
