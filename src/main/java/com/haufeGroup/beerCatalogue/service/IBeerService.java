package com.haufeGroup.beerCatalogue.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.haufeGroup.beerCatalogue.model.Beer;

@Service
public interface IBeerService {
	
	public List<Beer> getBeerListSortByCriteria(final Sort sortCriteria);

	public Beer getBeerById(final Long beerId);

	public Beer addBeer(final Beer newBeer);

	public Beer updateBeer(final Beer beerToModify);

	public void deleteBeerById(final Long beerId);

}
