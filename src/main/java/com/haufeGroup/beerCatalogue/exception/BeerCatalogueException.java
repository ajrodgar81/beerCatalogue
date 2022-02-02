package com.haufeGroup.beerCatalogue.exception;

public class BeerCatalogueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BeerCatalogueException() {
		super();
	}

	public BeerCatalogueException(String message) {
		super(message);
	}

}
