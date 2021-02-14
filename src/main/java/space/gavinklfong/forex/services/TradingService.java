package space.gavinklfong.forex.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import space.gavinklfong.forex.repos.TradeOrderRepos;
import space.gavinklfong.forex.models.TradeOrder;
import space.gavinklfong.forex.models.TradeOrderRequest;


@Component
public class TradingService {

	@Autowired
	private TradeOrderRepos tradeOrderRepos;
	
	@Autowired
	private RateService rateService;
	
	
	public Mono<TradeOrder> postOrder(TradeOrderRequest request) {
		
		// validate rate
		if (rateService.validateRate(request.getRate()).
		
		
		// post order
		
		
		return Mono.just(new TradeOrder());
	}
}
