package space.gavinklfong.forex.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import space.gavinklfong.forex.repos.TradeDealRepo;


@Component
public class TradeService {

	@Autowired
	private TradeDealRepo tradeOrderRepos;
	
	@Autowired
	private RateService rateService;
	
	
}
