package space.gavinklfong.forex.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import space.gavinklfong.forex.models.ForexRateBooking;

@Repository
public interface ForexRateBookingRepo extends CrudRepository <ForexRateBooking, Long>{

	List<ForexRateBooking> findByBookingRef(String bookingRef);
	
	
}
