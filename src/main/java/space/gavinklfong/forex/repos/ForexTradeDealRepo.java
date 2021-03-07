package space.gavinklfong.forex.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import space.gavinklfong.forex.models.ForexTradeDeal;

public interface ForexTradeDealRepo extends CrudRepository<ForexTradeDeal, Long> {

	/**
	 * Retrieve list of trade deal record by customer id
	 * 
	 * @param customerId
	 * @return
	 */
	List<ForexTradeDeal> findByCustomerId(Long customerId);
}
