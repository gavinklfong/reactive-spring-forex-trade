package space.gavinklfong.forex.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import space.gavinklfong.forex.repos.TradeDealRepo;
import space.gavinklfong.forex.models.TradeDeal;


@Component
public class TradeService {

	@Autowired
	private TradeDealRepo tradeOrderRepos;
	
	@Autowired
	private RateService rateService;
	
	
}
