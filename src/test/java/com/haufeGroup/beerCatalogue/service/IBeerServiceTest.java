package com.haufeGroup.beerCatalogue.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;

import com.haufeGroup.beerCatalogue.exception.BeerServiceException;
import com.haufeGroup.beerCatalogue.mapper.BeerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

@ExtendWith(MockitoExtension.class)
public class IBeerServiceTest {

	private static final long KNOWN_BEER_ID = 1;

	private static final long REMOVED_BEER_ID = 2;

	private static final long UNKNOWN_BEER_ID = 11111;

	private static final long KNOWN_MANUFACTURER_ID = 1;

	private static final long REMOVED_MANUFACTURER_ID = 2;

	private static final long ANOTHER_KNOWN_MANUFACTURER_ID = 3;

	private static final long UNKNOWN_MANUFACTURER_ID = 11111;

	@Mock
	private BeerRepository beerRepository;

	@Mock
	private BeerMapper modelMapper;

	@Mock
	private ManufacturerRepository manufacturerRepository;

	@InjectMocks
	private IBeerService testSubject = new BeerServiceImpl();

	private static SortExtractor sortExtractor;

	@BeforeAll
	public static void initialize() {
		sortExtractor = new SortExtractor();
	}

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void getBeerListSortByValidCriteria() {
		Sort sortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
		Mockito.when(beerRepository.findAll(sortCriteria)).thenReturn(Arrays.asList(createDefaultBeer()));
		assertThat(testSubject.getBeerListSortByCriteria(sortCriteria))
				.as("check that a beer list is returned when the sort criteria is valid").isNotEmpty();
	}

	@Test
	public void getBeerListSortByInvalidCriteria() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Sort sortCriteria = sortExtractor.extractSortCriteria(new String[] { "unknownName", "asc" });
			Mockito.when(beerRepository.findAll(sortCriteria)).thenThrow(PropertyReferenceException.class);
			testSubject.getBeerListSortByCriteria(sortCriteria);
		});

	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToExistingBeer() {
		Mockito.when(beerRepository.findById(KNOWN_BEER_ID)).thenReturn(Optional.of(createDefaultBeer()));
		assertThat(testSubject.getBeerById(KNOWN_BEER_ID)).as("check that the beer is returned")
				.isEqualTo(createDefaultBeer());
	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToNonExistingBeer() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(UNKNOWN_BEER_ID)).thenReturn(Optional.empty());
			testSubject.getBeerById(UNKNOWN_BEER_ID);
		});
	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToBeerMarkedAsDeleted() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(REMOVED_BEER_ID)).thenReturn(Optional.empty());
			testSubject.getBeerById(REMOVED_BEER_ID);
		});
	}

	@Test
	public void addNewBeerToExistingManufacturer() {
		Beer newBear = createDefaultBeerWithoutId();
		Beer savedBeer = createDefaultBeer();
		newBear.getManufacturer().setId(KNOWN_MANUFACTURER_ID);
		Mockito.when(manufacturerRepository.findById(KNOWN_MANUFACTURER_ID))
				.thenReturn(Optional.of(newBear.getManufacturer()));
		Mockito.when(beerRepository.save(newBear)).thenReturn(savedBeer);
		assertThat(testSubject.addBeer(newBear)).as("check that the beer was created").isEqualTo(savedBeer);
	}

	@Test
	public void addNewBeerToNonExistingManufacturer() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Beer newBear = createDefaultBeerWithoutId();
			newBear.getManufacturer().setId(UNKNOWN_MANUFACTURER_ID);
			Mockito.when(manufacturerRepository.findById(UNKNOWN_MANUFACTURER_ID)).thenReturn(Optional.empty());
			testSubject.addBeer(newBear);
		});
	}

	@Test
	public void addNewBeerToManufacturerMarkedAsDeleted() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Beer newBear = createDefaultBeerWithoutId();
			newBear.getManufacturer().setId(REMOVED_MANUFACTURER_ID);
			Mockito.when(manufacturerRepository.findById(REMOVED_MANUFACTURER_ID)).thenReturn(Optional.empty());
			testSubject.addBeer(newBear);
		});
	}

	@Test
	public void addNewBeerWhenTheManufacturerIsNotProvided() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Beer newBear = createDefaultBeerWithoutId();
			newBear.setManufacturer(null);
			testSubject.addBeer(newBear);
		});
	}

	@Test
	public void modifyABeerThatExists() {
		Beer oldBeer = createDefaultBeer();
		Beer beerToModify = createDefaultModifiedBeer();
		Mockito.when(beerRepository.findById(beerToModify.getId())).thenReturn(Optional.of(oldBeer));
		Mockito.when(beerRepository.save(oldBeer)).thenReturn(beerToModify);
		assertThat(testSubject.updateBeer(beerToModify)).as("check that the beer was modified").isEqualTo(beerToModify);
		Mockito.verify(modelMapper).mergeEntity(beerToModify, oldBeer);
	}

	@Test
	public void modifyABeerThatNotExists() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(UNKNOWN_BEER_ID)).thenReturn(Optional.empty());
			Beer beerToModify = createDefaultBeerWithoutId();
			beerToModify.setId(UNKNOWN_BEER_ID);
			testSubject.updateBeer(beerToModify);
		});

	}

	@Test
	public void modifyABeerMarkedAsDeleted() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(REMOVED_BEER_ID)).thenReturn(Optional.empty());
			Beer beerToModify = createDefaultBeerWithoutId();
			beerToModify.setId(REMOVED_BEER_ID);
			testSubject.updateBeer(beerToModify);
		});

	}

	@Test
	public void modifyManufacturerOfExistingBeer() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Beer oldBeer = createDefaultBeer();
			oldBeer.getManufacturer().setId(KNOWN_MANUFACTURER_ID);
			Beer beerToModify = createDefaultBeer();
			beerToModify.getManufacturer().setId(ANOTHER_KNOWN_MANUFACTURER_ID);
			Mockito.when(beerRepository.findById(beerToModify.getId())).thenReturn(Optional.of(oldBeer));
			testSubject.updateBeer(beerToModify);
		});
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToExistingBeer() {
		Beer oldBeer = createDefaultBeer();
		Mockito.when(beerRepository.findById(oldBeer.getId())).thenReturn(Optional.of(oldBeer));
		testSubject.deleteBeerById(oldBeer.getId());
		Mockito.verify(beerRepository).delete(oldBeer);
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToNonExistingBeer() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(UNKNOWN_BEER_ID)).thenReturn(Optional.empty());
			testSubject.deleteBeerById(UNKNOWN_BEER_ID);
		});
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToBeerMarkedAsDeleted() {
		Assert.assertThrows(BeerServiceException.class, () -> {
			Mockito.when(beerRepository.findById(REMOVED_BEER_ID)).thenReturn(Optional.empty());
			testSubject.deleteBeerById(REMOVED_BEER_ID);
		});
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
