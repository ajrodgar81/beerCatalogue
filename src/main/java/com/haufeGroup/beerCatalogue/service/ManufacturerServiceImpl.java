package com.haufeGroup.beerCatalogue.service;

import java.util.NoSuchElementException;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.haufeGroup.beerCatalogue.exception.ManufacturerServiceException;
import com.haufeGroup.beerCatalogue.mapper.ManufacturerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;

@Service
@Validated
public class ManufacturerServiceImpl implements IManufacturerService {

	public static final String MANUFACTURER_NOT_FOUND_ERROR_MESSAGE = "the id provided not belongs to existing manufacturer.";

	public static final String INVALID_SORT_CRITERIA = "The sort criteria provided is not valid. Please check that the field exists and the provided order value is asc or desc.";

	public static final String MANUFACTURER_ID_PROVIDED_ERROR_MESSAGE = "it's not possible create a manufacturer with a specific id.";

	@Autowired
	ManufacturerRepository manufacturerRepository;

	@Autowired
	BeerRepository beerRepository;

	@Autowired
	ManufacturerMapper modelMapper;

	@Override
	public Page<Manufacturer> getAllManufacturesWithSortPagination(@NotNull final Pageable sortPageable) {
		try {
			return manufacturerRepository.findAll(sortPageable);
		} catch (PropertyReferenceException pre) {
			throw new ManufacturerServiceException(INVALID_SORT_CRITERIA);
		}
	}

	@Override
	public Manufacturer getManufacturerById(@NotNull final Long manufacturerId) {
		if (manufacturerRepository.existsById(manufacturerId)) {
			return manufacturerRepository.findById(manufacturerId).get();
		} else {
			throw new ManufacturerServiceException(MANUFACTURER_NOT_FOUND_ERROR_MESSAGE);
		}
	}

	@Override
	public Page<Beer> getManufacturerBeersWithSortPagination(@NotNull final Long manufacturerId,
			@NotNull final Pageable sortPageable) {
		checkThatManufacturerExists(manufacturerId);
		try {
			return beerRepository.findByManufacturerId(manufacturerId, sortPageable);
		} catch (PropertyReferenceException pre) {
			throw new ManufacturerServiceException(INVALID_SORT_CRITERIA);
		}
	}

	@Override
	public Manufacturer addNewManufacturer(@NotNull final Manufacturer newManufacturer) {
		checkThatManufacturerIdIsNotProvided(newManufacturer.getId());
		return manufacturerRepository.save(newManufacturer);
	}

	@Override
	public Manufacturer updateManufacturer(@NotNull final Manufacturer manufacturerToModify) {
		try {
			Manufacturer oldManufacturer = manufacturerRepository.findById(manufacturerToModify.getId()).orElseThrow();
			modelMapper.mergeEntity(manufacturerToModify, oldManufacturer);
			return manufacturerRepository.save(oldManufacturer);
		} catch (NoSuchElementException nsee) {
			throw new ManufacturerServiceException(MANUFACTURER_NOT_FOUND_ERROR_MESSAGE);
		} catch (Exception ex) {
			throw new ManufacturerServiceException(ex.getMessage());
		}
	}

	@Override
	public void deleteManufacturerById(@NotNull final Long manufacturerId) {
		checkThatManufacturerExists(manufacturerId);
		manufacturerRepository.deleteById(manufacturerId);
	}

	private void checkThatManufacturerIdIsNotProvided(final Long manufacturerId) {
		if (manufacturerId != null) {
			throw new ManufacturerServiceException(MANUFACTURER_ID_PROVIDED_ERROR_MESSAGE);
		}
	}

	private void checkThatManufacturerExists(final Long manufacturerId) {
		if (!manufacturerRepository.existsById(manufacturerId)) {
			throw new ManufacturerServiceException(MANUFACTURER_NOT_FOUND_ERROR_MESSAGE);
		}
	}
}
