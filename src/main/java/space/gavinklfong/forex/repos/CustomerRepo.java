package space.gavinklfong.forex.repos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import space.gavinklfong.forex.models.Customer;

public interface CustomerRepo extends ReactiveCrudRepository<Customer, Long> {

}
