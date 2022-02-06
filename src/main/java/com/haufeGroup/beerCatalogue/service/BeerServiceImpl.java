package com.haufeGroup.beerCatalogue.service;

import java.util.NoSuchElementException;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.haufeGroup.beerCatalogue.exception.BeerServiceException;
import com.haufeGroup.beerCatalogue.mapper.BeerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;

@Service
@Validated
public class BeerServiceImpl implements IBeerService {

	public static final String INVALID_SORT_CRITERIA = "The sort criteria provided is not valid. Please check that the field exists and the provided order value is asc or desc.";

	public static final String MODIFY_BEER_MANUFACTURER_ERROR_MESSAGE = "Modify manufacturer of existing beer is not allowed.";

	public static final String MANUFACTURER_NOT_FOUND_ERROR_MESSAGE = "the id provided not belongs to existing manufacturer.";

	public static final String BEER_NOT_FOUND_ERROR_MEESSAGE = "the provided id not belongs to existing beer.";

	public static final String BEER_ID_PROVIDED_ERROR_MESSAGE = "it's not possible create a beer with a specific id.";

	@Autowired
	private BeerRepository beerRepository;

	@Autowired
	private ManufacturerRepository manufacturerRepository;

	@Autowired
	private BeerMapper modelMapper;

	@Override
	public Page<Beer> getAllBeersWithSortPagination(@NotNull final Pageable pagingSort) {
		try {
			return beerRepository.findAll(pagingSort);
		} catch (PropertyReferenceException pre) {
			throw new BeerServiceException(INVALID_SORT_CRITERIA);
		} catch (Exception ex) {
			throw new BeerServiceException(ex.getMessage());
		}
	}

	@Override
	public Beer getBeerById(@NotNull final Long beerId) {
		checkThatTheBeerExists(beerId);
		return beerRepository.findById(beerId).get();
	}

	@Override
	public Beer addNewBeer(@NotNull final Beer newBeer) {
		checkThatBeerIdIsNotProvided(newBeer.getId());
		checkThatTheManufacturerExists(newBeer.getManufacturer());
		return beerRepository.save(newBeer);
	}

	@Override
	public Beer updateBeer(@NotNull final Beer beerToModify) {
		try {
			Beer oldBeer = beerRepository.findById(beerToModify.getId()).orElseThrow();
			checkThatManufacturerIsNotUpdated(oldBeer.getManufacturer(), beerToModify.getManufacturer());
			modelMapper.mergeEntity(beerToModify, oldBeer);
			return beerRepository.save(oldBeer);
		} catch (NoSuchElementException nsee) {
			throw new BeerServiceException(BEER_NOT_FOUND_ERROR_MEESSAGE);
		} catch (Exception ex) {
			throw new BeerServiceException(ex.getMessage());
		}
	}

	@Override
	public void deleteBeerById(@NotNull final Long beerId) {
		checkThatTheBeerExists(beerId);
		beerRepository.deleteById(beerId);
	}

	private void checkThatBeerIdIsNotProvided(final Long beerId) {
		if (beerId != null) {
			throw new BeerServiceException(BEER_ID_PROVIDED_ERROR_MESSAGE);
		}
	}

	private void checkThatTheBeerExists(final Long beerId) {
		if (!beerRepository.existsById(beerId)) {
			throw new BeerServiceException(BEER_NOT_FOUND_ERROR_MEESSAGE);
		}
	}

	private void checkThatManufacturerIsNotUpdated(@NotNull final Manufacturer oldBeerManufacturer,
			@NotNull final Manufacturer beerManufacturerToModify) {
		if (!beerManufacturerToModify.getId().equals(oldBeerManufacturer.getId())) {
			throw new BeerServiceException(MODIFY_BEER_MANUFACTURER_ERROR_MESSAGE);
		}
	}

	private void checkThatTheManufacturerExists(@NotNull final Manufacturer manufacturer) {
		if (manufacturer.getId() == null || !manufacturerRepository.existsById(manufacturer.getId())) {
			throw new BeerServiceException(MANUFACTURER_NOT_FOUND_ERROR_MESSAGE);
		}
	}
}
