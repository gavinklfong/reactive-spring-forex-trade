package space.gavinklfong.forex.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.apiclients.ForexRateApiClient;
import space.gavinklfong.forex.dto.ForexPricing;
import space.gavinklfong.forex.dto.ForexRate;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.dto.TradeAction;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.models.Customer;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.models.ForexRateBooking.ForexRateBookingBuilder;
import space.gavinklfong.forex.repos.CustomerRepo;
import space.gavinklfong.forex.repos.ForexRateBookingRepo;

@Slf4j
@Component
public class ForexRateService {
			
	@Autowired
	private ForexRateApiClient forexRateApiClient;
	
	@Autowired
	private ForexRateBookingRepo rateBookingRepo;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private ForexPricingService forexPriceService;
	
	@Value("${app.rate-booking-duration}")
	private long bookingDuration = 120l;
	
	
	/**
	 * Retrieve the latest rates for list of counter currencies
	 * 
	 * 1. Fetch a list of counter currency by the base currency	  
	 * 2. Verify if base currency is available for price retrieval
	 * 3. Consume Forex rate API to get the latest rate for the base currency
	 * 4. Filter the records by list of country currency available for price retrieval
	 * 5. Obtain Forex price for each currency pair 
	 * 
	 * Flow: 
	 * Mono<ApiResponse> 					[Retrieve from Forex Rate API] 
	 * --> Flux<Map.Entry<String, Double>>  [Extract rates from response]
	 * --> Flux<ForexRate> 					[Update rate based on price service]
	 * 
	 * @param baseCurrency
	 * @return Flux - Rate
	 * @throws InvalidRequestException 
	 */
	public Flux<ForexRate> fetchLatestRates(String baseCurrency) throws InvalidRequestException {
				
		List<ForexPricing> counterCurrencyPricingList = forexPriceService.findCounterCurrencies(baseCurrency);
		
		if (counterCurrencyPricingList.isEmpty())
			throw new InvalidRequestException("base currency", "Invalid base currency");
		
		
		return forexRateApiClient.fetchLatestRates(baseCurrency)
		.flatMapMany(resp -> Flux.fromIterable(resp.getRates().entrySet()))
		.filter(rate -> counterCurrencyPricingList.stream().anyMatch(p -> p.getCounterCurrency().equalsIgnoreCase(rate.getKey())))
		.doOnNext(rate -> log.debug(rate.toString()))
		.map(rate -> {
			Double value = rate.getValue();
			String key = rate.getKey();
			return forexPriceService.obtainForexPrice(baseCurrency, key, value);	
		});
	}
	
	/**
	 * Retrieve the latest forex rate for the given base currency and counter currency
	 * 
	 * @param baseCurrency
	 * @param counterCurrency
	 * @return Mono<ForexRate>
	 * @throws InvalidRequestException
	 */
	public Mono<ForexRate> fetchLatestRate(String baseCurrency, String counterCurrency) throws InvalidRequestException {
		
		List<ForexPricing> counterCurrencyPricingList = forexPriceService.findCounterCurrencies(baseCurrency);
		
		if (counterCurrencyPricingList.isEmpty())
			throw new InvalidRequestException("base currency", "Invalid base currency");			
		
		if (counterCurrencyPricingList.stream().noneMatch(c -> c.getCounterCurrency().equalsIgnoreCase(counterCurrency)))
			throw new InvalidRequestException("base currency", "Invalid counter currency");		
			
		return forexRateApiClient.fetchLatestRate(baseCurrency, counterCurrency)
				.map(resp -> 
						forexPriceService.obtainForexPrice(
									baseCurrency, 
									counterCurrency, 
									resp.getRates().get(counterCurrency)));
	}	
	
	
	/**
	 * Generate rate booking based on the latest rate and customer tier
	 * 
	 * 
	 * Flow:
	 * Mono<ApiResponse>					[Retrieve rate from forex API]
	 * --> Mono<ForexRate>					[Construct ForexRate]
	 * --> Mono<ForexRate>  			 	[Update rate by price service]
	 * --> Tuple[<ForexRate>, <Customer>] 	[Retrieve customer record and zip together]
	 * --> Mono<Forex>						[Adjust rate based on customer tier]
	 * --> Mono<ForexRateBooking>			[Build rate booking object]
	 * --> Mono<ForexRateBooking>			[Saved rate booking object with record id]
	 * 
	 * @param request
	 * @return Mono - RateBooking
	 * @throws InvalidRequestException 
	 */
	public Mono<ForexRateBooking> obtainBooking(ForexRateBookingReq request) throws InvalidRequestException {
		
		List<ForexPricing> forexPricingList = forexPriceService.findCounterCurrencies(request.getBaseCurrency());		
		if (forexPricingList.stream().noneMatch(p -> p.getCounterCurrency().equalsIgnoreCase(request.getCounterCurrency()))) {
			throw new InvalidRequestException("currency", "unknown currency pair");
		}
		
		return forexRateApiClient
				.fetchLatestRate(request.getBaseCurrency(), request.getCounterCurrency())
				.map(resp -> 
					forexPriceService.obtainForexPrice(
								request.getBaseCurrency(), 
								request.getCounterCurrency(), 
								resp.getRates().get(request.getCounterCurrency())))
				.zipWith(customerRepo.findById(request.getCustomerId()))
				.map(tuple -> adjustRateForCustomerTier(tuple.getT1(), tuple.getT2()))
				.map(rate -> buildRateBookingRecord(request, rate))
				.flatMap(booking -> rateBookingRepo.save(booking));
	}
		
	/**
	 * Validate whether Rate Booking by
	 *  1. Check if booking reference exists in repository
	 *  2. Check if the record is expired   
	 * 
	 * Flow:
	 * Flux<ForexRateBooking>		[Retrieve rate booking record from repos]
	 * --> Mono<ForexRateBooking>	[Convert Flux to Mono]
	 * --> Mono<Boolean>			[Validate rate booking record]
	 * 
	 * Return Mono<Boolean> false in case of exception such as no rate booking record found
	 * 
	 * @param rateBooking
	 * @return Validation result - true / false
	 */
	public Mono<Boolean> validateRateBooking(ForexRateBooking rateBooking) {
					
		log.debug("incoming rateBooking - " + rateBooking.toString());
		
		return rateBookingRepo.findByBookingRef(rateBooking.getBookingRef())
				.doOnNext(record -> log.debug(record.toString()))
				.map(record -> {
					if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
						log.debug("booking request already expired");
						return false;
					}

					if (!record.getBaseCurrency().equalsIgnoreCase(rateBooking.getBaseCurrency())
							|| !record.getCounterCurrency().equalsIgnoreCase(rateBooking.getCounterCurrency())) {
						return false;
					}

					if (record.getBaseCurrencyAmount().compareTo(rateBooking.getBaseCurrencyAmount()) != 0) {
						log.debug("amount is not the same");
						return false;
					}
					
					return true;
					
				})
				.onErrorReturn(Boolean.valueOf(false));
	}
	
	private ForexRate adjustRateForCustomerTier(ForexRate rate, Customer customer) {
	 	
		double adjustment = 0;
		
		// determine rate
		switch (customer.getTier()) {
		case 1:
			adjustment += CustomerRateTier.TIER1.rate;
			break;
		case 2:
			adjustment += CustomerRateTier.TIER2.rate;
			break;
		case 3:
			adjustment += CustomerRateTier.TIER3.rate;
			break;
		default:
			adjustment += CustomerRateTier.TIER4.rate;
		}
		
		return rate.withBuyRate(rate.getBuyRate() + adjustment).withSellRate(rate.getSellRate() + adjustment);					
	}
	
	private ForexRateBooking buildRateBookingRecord(ForexRateBookingReq request, ForexRate rate) {
		
		LocalDateTime timestamp = LocalDateTime.now();
		LocalDateTime expiryTime = timestamp.plusSeconds(bookingDuration);
		
		ForexRateBookingBuilder builder = ForexRateBooking.builder()
				.baseCurrency(request.getBaseCurrency())
				.counterCurrency(request.getCounterCurrency())
				.baseCurrencyAmount(request.getBaseCurrencyAmount())
				.customerId(request.getCustomerId())
				.tradeAction(request.getTradeAction())
				.bookingRef(UUID.randomUUID().toString())
				.timestamp(LocalDateTime.now())
				.expiryTime(timestamp.plusSeconds(bookingDuration));	
		
		if (request.getTradeAction() == TradeAction.SELL)
			builder.rate(rate.getSellRate());
		else if (request.getTradeAction() == TradeAction.BUY)
			builder.rate(rate.getBuyRate());
		else
			throw new RuntimeException("Unknown trade action");
	
		return builder.build();	
	}	
		
}
