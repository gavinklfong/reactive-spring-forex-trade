package space.gavinklfong.forex.repos;

import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import space.gavinklfong.forex.models.ForexTradeDeal;

public interface ForexTradeDealRepo extends ReactiveCrudRepository<ForexTradeDeal, Long> {

	/**
	 * Retrieve list of trade deal record by customer id
	 * 
	 * @param customerId
	 * @return
	 */
	Flux<ForexTradeDeal> findByCustomerId(Long customerId);
}
