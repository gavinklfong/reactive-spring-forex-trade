package space.gavinklfong.forex.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import space.gavinklfong.forex.models.RateBooking;

@Repository
public interface RateBookingRepo extends CrudRepository <RateBooking, Long>{

	List<RateBooking> findByBookingRef(String bookingRef);
	
	
}
