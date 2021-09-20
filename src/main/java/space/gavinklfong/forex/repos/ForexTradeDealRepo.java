package space.gavinklfong.forex.repos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.models.ForexTradeDeal;

public interface ForexTradeDealRepo extends ReactiveCrudRepository<ForexTradeDeal, Long> {

	/**
	 * Retrieve list of trade deal records by customer id
	 * 
	 * @param customerId
	 * @return 
	 */
	Flux<ForexTradeDeal> findByCustomerId(Long customerId);

	/**
	 * Retrieve list of trade deal records by customer id and deal reference
	 *
	 * @param customerId
	 * @param dealRef
	 * @return
	 */
	Flux<ForexTradeDeal> findByCustomerIdAndDealRef(Long customerId, String dealRef);
}
