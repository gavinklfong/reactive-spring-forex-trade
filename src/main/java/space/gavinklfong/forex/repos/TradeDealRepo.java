package space.gavinklfong.forex.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import space.gavinklfong.forex.models.TradeDeal;

public interface TradeDealRepo extends CrudRepository<TradeDeal, Long> {

	/**
	 * Retrieve list of trade deal record by customer id
	 * 
	 * @param customerId
	 * @return
	 */
	List<TradeDeal> findByCustomerId(Long customerId);
}
