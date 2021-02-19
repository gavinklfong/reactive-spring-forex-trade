package space.gavinklfong.forex.exceptions;

public class UnknownCustomerException extends InvalidRequestException {

	public UnknownCustomerException() {
		super("customerId", "Unknown customer");
		
	}
	
}
