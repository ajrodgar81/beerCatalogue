package com.haufeGroup.beerCatalogue.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.haufeGroup.beerCatalogue.model.Beer;

@Service
public class BeerServiceImpl implements IBeerService {

	@Override
	public Beer getBeerById(final Long beerId) {
		return Beer.builder().id(beerId).build();
	}

	@Override
	public Beer addBeer(final Beer newBeer) {
		newBeer.setId(1L);
		return newBeer;
	}

	@Override
	public Beer updateBeer(final Beer updatedBeer) {
		return updatedBeer;
	}

	@Override
	public void deleteBeer(final Long beerId) {
		// TODO
	}

	@Override
	public List<Beer> getBeersByManufacturerId(Long manufacturerId) {
		return Arrays.asList(Beer.builder().id(manufacturerId).build());
	}
}
