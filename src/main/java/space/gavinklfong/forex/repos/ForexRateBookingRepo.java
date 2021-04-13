package space.gavinklfong.forex.repos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;
import space.gavinklfong.forex.models.ForexRateBooking;

public interface ForexRateBookingRepo extends ReactiveCrudRepository <ForexRateBooking, Long>{

	/**
	 * Retrieve rate booking record by booking ref
	 * 
	 * @param customerId
	 * @return
	 */
	Mono<ForexRateBooking> findByBookingRef(String bookingRef);
	
}
