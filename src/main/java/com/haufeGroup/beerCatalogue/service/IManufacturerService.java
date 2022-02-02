package com.haufeGroup.beerCatalogue.service;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public interface IManufacturerService {

	public List<Manufacturer> getManufacturerListSortByCriteria(final Sort sortCriteria);

	public Manufacturer getManufacturerById(final Long manufacturerId);

	public List<Beer> getBeerListSortByCriteria(final Long manufacturerId, final Sort sortCriteria);

	public Manufacturer addManufacturer(final Manufacturer newManufacturer);

	public Manufacturer updateManufacturer(final Manufacturer manufacturerToModify);

	public void deleteManufacturerById(final Long manufacturerId);

}
