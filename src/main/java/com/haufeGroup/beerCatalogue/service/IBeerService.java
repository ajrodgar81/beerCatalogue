package com.haufeGroup.beerCatalogue.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haufeGroup.beerCatalogue.model.Beer;

@Service
public interface IBeerService {

	public Beer getBeerById(final Long beerId);

	public Beer addBeer(final Beer newBeer);

	public Beer updateBeer(final Beer updatedBeer);

	public void deleteBeer(final Long beerId);
	
	public List<Beer> getBeersByManufacturerId(final Long manufacturerId);

}
