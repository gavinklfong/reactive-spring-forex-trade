package space.gavinklfong.forex.repos;

import org.springframework.data.repository.CrudRepository;

import space.gavinklfong.forex.models.TradeOrder;

public interface TradeOrderRepos extends CrudRepository<TradeOrder, Long> {

}
