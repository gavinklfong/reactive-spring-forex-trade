package space.gavinklfong.forex.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import space.gavinklfong.forex.models.Rate;

@Repository
public interface ForexRepos extends CrudRepository <Rate, Long>{

}
