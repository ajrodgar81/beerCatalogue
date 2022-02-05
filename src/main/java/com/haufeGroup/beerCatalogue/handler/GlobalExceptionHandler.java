package com.haufeGroup.beerCatalogue.handler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.haufeGroup.beerCatalogue.exception.BeerCatalogueException;
import com.haufeGroup.beerCatalogue.exception.BeerServiceException;
import com.haufeGroup.beerCatalogue.exception.ManufacturerServiceException;
import com.haufeGroup.beerCatalogue.service.BeerServiceImpl;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", status.value());
		// Get all errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

		body.put("errors", errors);
		return new ResponseEntity<>(body, headers, status);
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.BAD_REQUEST);
		List<String> errors = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		body.put("errors", errors);
		return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ BeerServiceException.class })
	public ResponseEntity<Object> handleBeerServiceException(BeerCatalogueException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		HttpStatus httpErrorStatus = BeerServiceImpl.BEER_NOT_FOUND_ERROR_MEESSAGE.equals(ex.getMessage())
				? HttpStatus.NOT_FOUND
				: HttpStatus.BAD_REQUEST;
		addErrorToBody(body, httpErrorStatus, ex.getMessage());
		return new ResponseEntity<>(body, new HttpHeaders(), httpErrorStatus);
	}

	@ExceptionHandler({ ManufacturerServiceException.class })
	public ResponseEntity<Object> handleManufacturerServiceException(BeerCatalogueException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		HttpStatus httpErrorStatus = BeerServiceImpl.MANUFACTURER_NOT_FOUND_ERROR_MESSAGE.equals(ex.getMessage())
				? HttpStatus.NOT_FOUND
				: HttpStatus.BAD_REQUEST;
		addErrorToBody(body, httpErrorStatus, ex.getMessage());
		return new ResponseEntity<>(body, new HttpHeaders(), httpErrorStatus);
	}

	@ExceptionHandler({ BeerCatalogueException.class })
	public ResponseEntity<Object> handleBeerCatalogueException(BeerCatalogueException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		HttpStatus httpErrorStatus = HttpStatus.BAD_REQUEST;
		addErrorToBody(body, httpErrorStatus, ex.getMessage());
		return new ResponseEntity<>(body, new HttpHeaders(), httpErrorStatus);
	}

	private void addErrorToBody(final Map<String, Object> body, final HttpStatus httpErrorStatus,
			final String errorMessage) {
		body.put("timestamp", new Date());
		body.put("status", httpErrorStatus);
		body.put("error", errorMessage);
	}
}
