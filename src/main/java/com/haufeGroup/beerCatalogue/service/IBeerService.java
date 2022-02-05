package com.haufeGroup.beerCatalogue.service;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.haufeGroup.beerCatalogue.model.Beer;

public interface IBeerService {

	public Page<Beer> getAllBeersWithSortPagination(@NotNull final Pageable sortPageable);

	public Beer getBeerById(@NotNull final Long beerId);

	public Beer addNewBeer(@NotNull final Beer newBeer);

	public Beer updateBeer(@NotNull final Beer beerToModify);

	public void deleteBeerById(@NotNull final Long beerId);

}
