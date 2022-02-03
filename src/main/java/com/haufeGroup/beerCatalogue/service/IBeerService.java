package com.haufeGroup.beerCatalogue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.haufeGroup.beerCatalogue.model.Beer;

@Service
public interface IBeerService {
	
	public Page<Beer> getAllBeersWithSortPagination(final Pageable sortPageable);

	public Beer getBeerById(final Long beerId);

	public Beer addBeer(final Beer newBeer);

	public Beer updateBeer(final Beer beerToModify);

	public void deleteBeerById(final Long beerId);

}
