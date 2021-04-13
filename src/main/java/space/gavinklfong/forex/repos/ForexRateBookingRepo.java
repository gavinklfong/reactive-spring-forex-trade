package space.gavinklfong.forex.repos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.models.ForexRateBooking;

public interface ForexRateBookingRepo extends ReactiveCrudRepository <ForexRateBooking, Long>{

	Flux<ForexRateBooking> findByBookingRef(String bookingRef);
	
	Mono<ForexRateBooking> save(ForexRateBooking booking);
}
