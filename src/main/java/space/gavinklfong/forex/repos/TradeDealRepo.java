package space.gavinklfong.forex.repos;

import org.springframework.data.repository.CrudRepository;

import space.gavinklfong.forex.models.TradeDeal;

public interface TradeDealRepo extends CrudRepository<TradeDeal, Long> {

	Iterable<TradeDeal> findByCustomerId(Long customerId);
}
