package space.gavinklfong.forex.repos;

import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import space.gavinklfong.forex.models.ForexRateBooking;

public interface ForexRateBookingRepo extends ReactiveCrudRepository <ForexRateBooking, Long>{

	Flux<ForexRateBooking> findByBookingRef(String bookingRef);
	
	
}
