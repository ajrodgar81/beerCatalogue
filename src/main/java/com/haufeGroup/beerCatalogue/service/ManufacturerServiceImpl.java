package com.haufeGroup.beerCatalogue.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

import com.haufeGroup.beerCatalogue.exception.ManufacturerServiceException;
import com.haufeGroup.beerCatalogue.mapper.ManufacturerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;

@Service
public class ManufacturerServiceImpl implements IManufacturerService {

	public static final String MANUFACTURER_NOT_FOUND_ERROR_MESSAGE = "the id provided not belongs to existing manufacturer.";
	public static final String INVALID_SORT_CRITERIA = "The sort criteria provided is not valid. Please check that the field exists and the provided order value is asc or desc.";

	@Autowired
	ManufacturerRepository manufacturerRepository;

	@Autowired
	BeerRepository beerRepository;

	@Autowired
	ManufacturerMapper modelMapper;

	@Override
	public List<Manufacturer> getManufacturerListSortByCriteria(final Sort sortCriteria) {
		try {
			return manufacturerRepository.findAll(sortCriteria);
		} catch (PropertyReferenceException pre) {
			throw new ManufacturerServiceException(INVALID_SORT_CRITERIA);
		}
	}

	@Override
	public Manufacturer getManufacturerById(final Long manufacturerId) {
		return manufacturerRepository.findById(manufacturerId).orElseThrow(ManufacturerServiceException::new);
	}

	@Override
	public List<Beer> getBeerListSortByCriteria(final Long manufacturerId, final Sort sortCriteria) {
		checkThatManufacturerExists(manufacturerId);
		try {
			return beerRepository.findByManufacturerId(manufacturerId, sortCriteria);
		} catch (PropertyReferenceException pre) {
			throw new ManufacturerServiceException(INVALID_SORT_CRITERIA);
		}
	}

	private void checkThatManufacturerExists(final Long manufacturerId) {
		if (!manufacturerRepository.existsById(manufacturerId)) {
			throw new ManufacturerServiceException(MANUFACTURER_NOT_FOUND_ERROR_MESSAGE);
		}
	}

	@Override
	public Manufacturer addManufacturer(final Manufacturer newManufacturer) {
		return manufacturerRepository.save(newManufacturer);
	}

	@Override
	public Manufacturer updateManufacturer(final Manufacturer manufacturerToModify) {
		Manufacturer oldManufacturer = getManufacturerById(manufacturerToModify.getId());
		modelMapper.mergeEntity(manufacturerToModify, oldManufacturer);
		return manufacturerRepository.save(oldManufacturer);
	}

	@Override
	public void deleteManufacturerById(final Long manufacturerId) {
		manufacturerRepository.delete(getManufacturerById(manufacturerId));
	}

}
