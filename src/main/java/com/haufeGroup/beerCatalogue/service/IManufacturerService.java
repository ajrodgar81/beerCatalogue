package com.haufeGroup.beerCatalogue.service;

import java.util.List;

import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public interface IManufacturerService {

	public List<Manufacturer> getManufacturers();

	public Manufacturer getManufacturerById(final Long manufacturerId);

	public List<Beer> getBeerList(Long manufacturerId);

	public Manufacturer addManufacturer(final Manufacturer newManufacturer);

	public Manufacturer updateManufacturer(final Manufacturer updatedManufacturer);

	public void deleteManufacturer(final Long manufacturerId);

}
