package com.haufeGroup.beerCatalogue.service;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public interface IManufacturerService {

	public Page<Manufacturer> getAllManufacturesWithSortPagination(@NotNull final Pageable sortPageable);

	public Manufacturer getManufacturerById(@NotNull final Long manufacturerId);

	public Page<Beer> getManufacturerBeersWithSortPagination(@NotNull final Long manufacturerId,
			@NotNull final Pageable sortPageable);

	public Manufacturer addNewManufacturer(@NotNull final Manufacturer newManufacturer);

	public Manufacturer updateManufacturer(@NotNull final Manufacturer manufacturerToModify);

	public void deleteManufacturerById(@NotNull final Long manufacturerId);

}
