package com.haufeGroup.beerCatalogue.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

@Service
public class ManufacturerServiceImpl implements IManufacturerService {

	@Autowired
	private IBeerService beerService;

	@Override
	public List<Manufacturer> getManufacturers() {
		return Arrays.asList(Manufacturer.builder().id(1l).build());
	}

	@Override
	public Manufacturer getManufacturerById(final Long manufacturerId) {
		return Manufacturer.builder().id(manufacturerId).build();
	}

	@Override
	public List<Beer> getBeerList(Long manufacturerId) {
		return beerService.getBeersByManufacturerId(manufacturerId);
	}

	@Override
	public Manufacturer addManufacturer(final Manufacturer newManufacturerDto) {
		newManufacturerDto.setId(1L);
		return newManufacturerDto;
	}

	@Override
	public Manufacturer updateManufacturer(final Manufacturer updatedManufacturerDto) {
		return updatedManufacturerDto;
	}

	@Override
	public void deleteManufacturer(final Long manufacturerId) {
		// TODO
	}

}
