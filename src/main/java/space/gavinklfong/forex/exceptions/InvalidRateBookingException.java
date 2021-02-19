package space.gavinklfong.forex.exceptions;

public class InvalidRateBookingException extends InvalidRequestException {

	public InvalidRateBookingException() {
		super("RateBooking", "Invalid Rate Booking");
		
	}
}
