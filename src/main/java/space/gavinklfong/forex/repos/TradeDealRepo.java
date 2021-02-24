package space.gavinklfong.forex.repos;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import space.gavinklfong.forex.models.TradeDeal;

public interface TradeDealRepo extends ReactiveCrudRepository<TradeDeal, Long> {

	/**
	 * Retrieve list of trade deal record by customer id
	 * 
	 * @param customerId
	 * @return
	 */
	@Query("SELECT * FROM forex_trade_deal WHERE customer_id = :customerid")
	Flux<TradeDeal> findByCustomerId(Long customerId);
}
