package com.haufeGroup.beerCatalogue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public interface IManufacturerService {

	public Page<Manufacturer> getAllManufacturesWithSortPagination(final Pageable sortPageable);

	public Manufacturer getManufacturerById(final Long manufacturerId);

	public Page<Beer> getManufacturerBeersWithSortPagination(final Long manufacturerId, final Pageable sortPageable);

	public Manufacturer addManufacturer(final Manufacturer newManufacturer);

	public Manufacturer updateManufacturer(final Manufacturer manufacturerToModify);

	public void deleteManufacturerById(final Long manufacturerId);

}
