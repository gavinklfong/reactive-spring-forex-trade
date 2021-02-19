package space.gavinklfong.forex.repos;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import space.gavinklfong.forex.models.TradeDeal;

public interface TradeDealRepo extends CrudRepository<TradeDeal, Long> {

	List<TradeDeal> findByCustomerId(Long customerId);
}
