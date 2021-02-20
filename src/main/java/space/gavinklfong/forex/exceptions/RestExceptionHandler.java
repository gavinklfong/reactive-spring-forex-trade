package space.gavinklfong.forex.exceptions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class RestExceptionHandler {
	
	private static Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
	
	@ExceptionHandler({InvalidRequestException.class})
	public ResponseEntity<ErrorBody> handleInvalidRequestException(InvalidRequestException ex) {
	
		List<ObjectError> errors = ex.getErrors();
		List<ErrorMessage> errorMessages = errors.stream()
		.map(error -> { 
			if (error instanceof FieldError) {
				FieldError fieldError = (FieldError) error;
				return new ErrorMessage(fieldError.getField(), fieldError.getDefaultMessage());			
			} else {
				return new ErrorMessage(error.getCode(), error.getDefaultMessage());							
			}
		})
		.collect(Collectors.toList());
		
		return new ResponseEntity<>(new ErrorBody(errorMessages), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({WebExchangeBindException.class})
	public ResponseEntity<ErrorBody> handleWebExchangeBindException(WebExchangeBindException ex) {
		
		List<ObjectError> errors = ex.getAllErrors();
		List<ErrorMessage> errorMessages = errors.stream()
		.map(error -> { 
			if (error instanceof FieldError) {
				FieldError fieldError = (FieldError) error;
				return new ErrorMessage(fieldError.getField(), fieldError.getDefaultMessage());			
			} else {
				return new ErrorMessage(error.getCode(), error.getDefaultMessage());							
			}
		})
		.collect(Collectors.toList());
		
		return new ResponseEntity<>(new ErrorBody(errorMessages), HttpStatus.BAD_REQUEST);
	}
	
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorBody> handleInternalServerError(Exception ex) {
		
		List<ErrorMessage> errorMessages = Arrays.asList(new ErrorMessage("exception", ex.getMessage()));

		
		return new ResponseEntity<>(new ErrorBody(errorMessages), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorBody> handleNotFound(Exception ex) {
		
		List<ErrorMessage> errorMessages = Arrays.asList(new ErrorMessage("exception", "Resource Not Found"));

		
		return new ResponseEntity<>(new ErrorBody(errorMessages), HttpStatus.NOT_FOUND);
	}

}
