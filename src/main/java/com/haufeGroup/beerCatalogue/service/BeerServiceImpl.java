package com.haufeGroup.beerCatalogue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

import com.haufeGroup.beerCatalogue.exception.BeerServiceException;
import com.haufeGroup.beerCatalogue.mapper.BeerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;

@Service
public class BeerServiceImpl implements IBeerService {

	public static final String INVALID_SORT_CRITERIA = "The sort criteria provided is not valid. Please check that the field exists and the provided order value is asc or desc.";

	public static final String MODIFY_BEER_MANUFACTURER_ERROR_MESSAGE = "Modify manufacturer of existing beer is not allowed.";

	@Autowired
	private BeerRepository beerRepository;

	@Autowired
	private ManufacturerRepository manufacturerRepository;

	@Autowired
	private BeerMapper modelMapper;

	@Override
	public Page<Beer> getAllBeersWithSortPagination(final Pageable pagingSort) {
		try {
			return beerRepository.findAll(pagingSort);
		} catch (PropertyReferenceException pre) {
			throw new BeerServiceException(INVALID_SORT_CRITERIA);
		} catch (Exception ex) {
			throw new BeerServiceException(ex.getMessage());
		}
	}

	@Override
	public Beer getBeerById(final Long id) {
		return beerRepository.findById(id).orElseThrow(BeerServiceException::new);
	}

	@Override
	public Beer addBeer(final Beer newBeer) {
		checkThatTheManufacturerExists(newBeer.getManufacturer());
		return beerRepository.save(newBeer);
	}

	private void checkThatTheManufacturerExists(final Manufacturer manufacturer) {
		if (manufacturer == null || manufacturer.getId() == null) {
			throw new BeerServiceException();
		}
		manufacturerRepository.findById(manufacturer.getId()).orElseThrow(BeerServiceException::new);
	}

	@Override
	public Beer updateBeer(final Beer beerToModify) {
		Beer oldBeer = getBeerById(beerToModify.getId());
		checkThatManufacturerIsNotUpdated(oldBeer, beerToModify);
		modelMapper.mergeEntity(beerToModify, oldBeer);
		return beerRepository.save(oldBeer);
	}

	private void checkThatManufacturerIsNotUpdated(Beer oldBeer, Beer beerToModify) {
		if (beerToModify.getManufacturer() != null
				&& !beerToModify.getManufacturer().getId().equals(oldBeer.getManufacturer().getId())) {
			throw new BeerServiceException(MODIFY_BEER_MANUFACTURER_ERROR_MESSAGE);
		}
	}

	@Override
	public void deleteBeerById(final Long id) {
		beerRepository.delete(getBeerById(id));
	}

}
