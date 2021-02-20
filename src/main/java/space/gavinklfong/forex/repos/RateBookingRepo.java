package space.gavinklfong.forex.repos;

import java.util.List;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import space.gavinklfong.forex.models.RateBooking;

public interface RateBookingRepo extends ReactiveCrudRepository <RateBooking, Long>{

	@Query("SELECT * FROM forex_rate_booking WHERE booking_ref = :bookingref")
	Flux<RateBooking> findByBookingRef(String bookingRef);
	
}
