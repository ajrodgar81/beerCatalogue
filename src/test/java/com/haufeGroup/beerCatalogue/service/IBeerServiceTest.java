package com.haufeGroup.beerCatalogue.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.haufeGroup.beerCatalogue.exception.BeerServiceException;
import com.haufeGroup.beerCatalogue.mapper.BeerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IBeerServiceTest {

	private static final long KNOWN_BEER_ID = 1;

	private static final long REMOVED_BEER_ID = 2;

	private static final long UNKNOWN_BEER_ID = 11111;

	private static final long KNOWN_MANUFACTURER_ID = 1;

	private static final long REMOVED_MANUFACTURER_ID = 2;

	private static final long ANOTHER_KNOWN_MANUFACTURER_ID = 3;

	private static final long UNKNOWN_MANUFACTURER_ID = 11111;

	private static final int PAGE_INDEX = 0;

	private static final int PAGE_SIZE = 3;

	@MockBean
	private BeerRepository beerRepository;

	@MockBean
	private BeerMapper modelMapper;

	@MockBean
	private ManufacturerRepository manufacturerRepository;

	@Autowired
	private IBeerService testSubject;

	private static SortExtractor sortExtractor;

	@BeforeAll
	public static void initialize() {
		sortExtractor = new SortExtractor();
	}

	@Test
	public void getAllBeersWithSortPaginationWhenSortByValidCriteria() {
		Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
		Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
		Mockito.when(beerRepository.findAll(sortPageable)).thenReturn(createDefaultPageEntity());
		assertThat(testSubject.getAllBeersWithSortPagination(sortPageable))
				.as("check that a beer page is returned when the page sort criteria is valid").isNotEmpty();
	}

	@Test
	public void getAllBeersWithSortPaginationWhenSortByInvalidCriteria() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Sort invalidSortCriteria = sortExtractor.extractSortCriteria(new String[] { "unknownName", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, invalidSortCriteria);
			Mockito.when(beerRepository.findAll(sortPageable)).thenThrow(PropertyReferenceException.class);
			testSubject.getAllBeersWithSortPagination(sortPageable);
		});
	}

	@Test
	public void getAllBeersWithSortPaginationWhenTheSortPaginationCriteriaProvidedIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class,
				() -> testSubject.getAllBeersWithSortPagination(null));
	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToExistingBeer() {
		Mockito.when(beerRepository.existsById(KNOWN_BEER_ID)).thenReturn(true);
		Mockito.when(beerRepository.findById(KNOWN_BEER_ID)).thenReturn(Optional.of(createDefaultBeer()));
		assertThat(testSubject.getBeerById(KNOWN_BEER_ID)).as("check that the beer is returned")
				.isEqualTo(createDefaultBeer());
	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToNonExistingBeer() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.existsById(UNKNOWN_BEER_ID)).thenReturn(false);
			testSubject.getBeerById(UNKNOWN_BEER_ID);
		});
	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToBeerMarkedAsDeleted() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.existsById(REMOVED_BEER_ID)).thenReturn(false);
			testSubject.getBeerById(REMOVED_BEER_ID);
		});
	}

	@Test
	public void getBeerByIdWhenTheIdIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.getBeerById(null));
	}

	@Test
	public void addNewBeerWhenTheNewBeerIdIsNotProvidedAndTheManufacturerExists() {
		Beer newBeer = createDefaultBeerWithoutId();
		Beer savedBeer = createDefaultBeer();
		Mockito.when(manufacturerRepository.existsById(KNOWN_MANUFACTURER_ID)).thenReturn(true);
		Mockito.when(beerRepository.save(newBeer)).thenReturn(savedBeer);
		assertThat(testSubject.addNewBeer(newBeer)).as("check that the beer was created").isEqualTo(savedBeer);
	}

	@Test
	public void addNewBeerWhenTheNewBeerIdIsNotProvidedAndTheManufacturerNotExists() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Beer newBeer = createDefaultBeerWithoutId();
			newBeer.getManufacturer().setId(UNKNOWN_MANUFACTURER_ID);
			Mockito.when(manufacturerRepository.existsById(UNKNOWN_MANUFACTURER_ID)).thenReturn(false);
			testSubject.addNewBeer(newBeer);
		});
	}

	@Test
	public void addNewBeerWhenTheNewBeerIdIsNotProvidedAndTheManufacturerIsNotProvided() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Beer newBeer = createDefaultBeerWithoutId();
			newBeer.getManufacturer().setId(null);
			testSubject.addNewBeer(newBeer);
		});
	}

	@Test
	public void addNewBeerWhenWhenTheNewBeerIdIsNotProvidedAndTheManufacturerIsMarkedAsDeleted() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Beer newBeer = createDefaultBeerWithoutId();
			newBeer.getManufacturer().setId(REMOVED_MANUFACTURER_ID);
			Mockito.when(manufacturerRepository.existsById(REMOVED_MANUFACTURER_ID)).thenReturn(false);
			testSubject.addNewBeer(newBeer);
		});
	}

	@Test
	public void addNewBeerWhenTheNewBeerIdIsProvided() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			testSubject.addNewBeer(createDefaultBeer());
		});
	}

	@Test
	public void updateABeerThatExists() {
		Beer oldBeer = createDefaultBeer();
		Beer beerToModify = createDefaultModifiedBeer();
		Mockito.when(beerRepository.findById(beerToModify.getId())).thenReturn(Optional.of(oldBeer));
		Mockito.when(beerRepository.save(oldBeer)).thenReturn(beerToModify);
		assertThat(testSubject.updateBeer(beerToModify)).as("check that the beer was modified").isEqualTo(beerToModify);
		Mockito.verify(modelMapper).mergeEntity(beerToModify, oldBeer);
	}

	@Test
	public void updateABeerThatNotExists() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(UNKNOWN_BEER_ID)).thenReturn(Optional.empty());
			Beer beerToModify = createDefaultBeerWithoutId();
			beerToModify.setId(UNKNOWN_BEER_ID);
			testSubject.updateBeer(beerToModify);
		});

	}

	@Test
	public void updateABeerMarkedAsDeleted() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(REMOVED_BEER_ID)).thenReturn(Optional.empty());
			Beer beerToModify = createDefaultBeerWithoutId();
			beerToModify.setId(REMOVED_BEER_ID);
			testSubject.updateBeer(beerToModify);
		});

	}

	@Test
	public void updateManufacturerOfExistingBeer() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Beer oldBeer = createDefaultBeer();
			oldBeer.getManufacturer().setId(KNOWN_MANUFACTURER_ID);
			Beer beerToModify = createDefaultBeer();
			beerToModify.getManufacturer().setId(ANOTHER_KNOWN_MANUFACTURER_ID);
			Mockito.when(beerRepository.findById(beerToModify.getId())).thenReturn(Optional.of(oldBeer));
			testSubject.updateBeer(beerToModify);
		});
	}

	@Test
	public void updateABeerWhenTheBeerToModifyIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.updateBeer(null));
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToExistingBeer() {
		Mockito.when(beerRepository.existsById(KNOWN_BEER_ID)).thenReturn(true);
		testSubject.deleteBeerById(KNOWN_BEER_ID);
		Mockito.verify(beerRepository).deleteById(KNOWN_BEER_ID);
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToNonExistingBeer() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.existsById(UNKNOWN_BEER_ID)).thenReturn(false);
			testSubject.deleteBeerById(UNKNOWN_BEER_ID);
		});
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToBeerMarkedAsDeleted() {
		Assertions.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.existsById(REMOVED_BEER_ID)).thenReturn(false);
			testSubject.deleteBeerById(REMOVED_BEER_ID);
		});
	}

	@Test
	public void deleteBeerByIdWhenTheIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.deleteBeerById(null));
	}

	private Page<Beer> createDefaultPageEntity() {
		return new PageImpl<Beer>(List.of(createDefaultBeer()));
	}

	private Beer createDefaultModifiedBeer() {
		Beer beer = createDefaultBeer();
		beer.setDescription("updatedDescription");
		return beer;
	}

	private Beer createDefaultBeerWithoutId() {
		Beer beer = new Beer();
		beer.setName("beerName");
		beer.setGraduation("graduation");
		beer.setDescription("description");
		beer.setType("beerType");
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setId(KNOWN_MANUFACTURER_ID);
		beer.setManufacturer(manufacturer);
		return beer;
	}

	private Beer createDefaultBeer() {
		Beer beer = createDefaultBeerWithoutId();
		beer.setId(KNOWN_BEER_ID);
		return beer;
	}
}
