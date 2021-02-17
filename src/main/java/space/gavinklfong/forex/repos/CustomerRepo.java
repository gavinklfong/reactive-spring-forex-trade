package space.gavinklfong.forex.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import space.gavinklfong.forex.models.Customer;

@Repository
public interface CustomerRepo extends CrudRepository<Customer, Long> {

}
