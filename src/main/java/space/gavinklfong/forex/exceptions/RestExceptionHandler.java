package space.gavinklfong.forex.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
	
	@ExceptionHandler({InvalidRequestException.class})
	public ResponseEntity<ErrorBody> handleInvalidRequestException(InvalidRequestException ex) {
	
		List<ObjectError> errors = ex.getErrors();
		List<ErrorMessage> errorMessages = errors.stream()
		.map(error -> new ErrorMessage(error.getObjectName(), error.getDefaultMessage()))
		.collect(Collectors.toList());
		
		return new ResponseEntity<>(new ErrorBody(errorMessages), HttpStatus.BAD_REQUEST);
	}

}
