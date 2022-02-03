package com.haufeGroup.beerCatalogue.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;

import com.haufeGroup.beerCatalogue.exception.ManufacturerServiceException;
import com.haufeGroup.beerCatalogue.mapper.ManufacturerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

@ExtendWith(MockitoExtension.class)
public class IManufacturerServiceTest {

	private static final long KNOWN_MANUFACTURER_ID = 1;

	private static final long REMOVED_MANUFACTURER_ID = 2;

	private static final long UNKNOWN_MANUFACTURER_ID = 11111;

	private static final int PAGE_INDEX = 0;

	private static final int PAGE_SIZE = 3;

	@Mock
	private ManufacturerRepository manufacturerRepository;

	@Mock
	private BeerRepository beerRepository;

	@Mock
	private ManufacturerMapper modelMapper;

	@InjectMocks
	private IManufacturerService testSubject = new ManufacturerServiceImpl();

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
	public void getAllManufacturersWithSortPaginationWhenTheRelatedSortCriteriaIsValid() {
		Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
		Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
		Mockito.when(manufacturerRepository.findAll(sortPageable)).thenReturn(createDefaultManufacturerPage());
		assertThat(testSubject.getAllManufacturesWithSortPagination(sortPageable))
				.as("check that a manufacturer page is returned when the page sort criteria is valid").isNotEmpty();
	}

	@Test
	public void getAllManufacturersWithSortPaginationWhenTheRelatedSortCriteriaIsInvalid() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Sort invalidSortCriteria = sortExtractor.extractSortCriteria(new String[] { "unknownName", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, invalidSortCriteria);
			Mockito.when(manufacturerRepository.findAll(sortPageable)).thenThrow(PropertyReferenceException.class);
			testSubject.getAllManufacturesWithSortPagination(sortPageable);
		});
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExistsAndTheSortCriteriaIsValid() {
		Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
		Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
		Mockito.when(manufacturerRepository.existsById(KNOWN_MANUFACTURER_ID)).thenReturn(true);
		Mockito.when(beerRepository.findByManufacturerId(KNOWN_MANUFACTURER_ID, sortPageable))
				.thenReturn(createDefaultBeerPage());
		assertThat(testSubject.getManufacturerBeersWithSortPagination(KNOWN_MANUFACTURER_ID, sortPageable)).as(
				"check that the beer page of the related manufacturer is returned when the manufacturer exists and the page sort criteria is valid")
				.isNotEmpty();
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerNotExists() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
			Mockito.when(manufacturerRepository.existsById(UNKNOWN_MANUFACTURER_ID)).thenReturn(false);
			testSubject.getManufacturerBeersWithSortPagination(UNKNOWN_MANUFACTURER_ID, sortPageable);
		});
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerIsMarkedAsDeleted() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
			Mockito.when(manufacturerRepository.existsById(REMOVED_MANUFACTURER_ID)).thenReturn(false);
			testSubject.getManufacturerBeersWithSortPagination(REMOVED_MANUFACTURER_ID, sortPageable);
		});
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExistsButTheSortCriteriaIsInvalid() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Sort invalidSortCriteria = sortExtractor.extractSortCriteria(new String[] { "unknownName", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, invalidSortCriteria);
			Mockito.when(manufacturerRepository.existsById(KNOWN_MANUFACTURER_ID)).thenReturn(true);
			Mockito.when(beerRepository.findByManufacturerId(KNOWN_MANUFACTURER_ID, sortPageable))
					.thenThrow(PropertyReferenceException.class);
			testSubject.getManufacturerBeersWithSortPagination(KNOWN_MANUFACTURER_ID, sortPageable);
		});

	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExistsButNoBeersWereFound() {
		Sort invalidSortCriteria = sortExtractor.extractSortCriteria(new String[] { "unknownName", "asc" });
		Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, invalidSortCriteria);
		Mockito.when(manufacturerRepository.existsById(KNOWN_MANUFACTURER_ID)).thenReturn(true);
		Mockito.when(beerRepository.findByManufacturerId(KNOWN_MANUFACTURER_ID, sortPageable)).thenReturn(Page.empty());
		assertThat(testSubject.getManufacturerBeersWithSortPagination(KNOWN_MANUFACTURER_ID, sortPageable))
				.as("check that an empty beer page is returned when no beers were found for the related manufacturer")
				.isEmpty();
		testSubject.getManufacturerBeersWithSortPagination(KNOWN_MANUFACTURER_ID, sortPageable);
	}

	@Test
	public void getManufacturerByIdWhenTheIdBelongsToExistingManufacturer() {
		Mockito.when(manufacturerRepository.findById(KNOWN_MANUFACTURER_ID))
				.thenReturn(Optional.of(createDefaultManufacturer()));
		assertThat(testSubject.getManufacturerById(KNOWN_MANUFACTURER_ID)).as("check that the manufacturer is returned")
				.isEqualTo(createDefaultManufacturer());
	}

	@Test
	public void getManufacturerByIdWhenTheIdBelongsToNonExistingManufacturer() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(UNKNOWN_MANUFACTURER_ID)).thenReturn(Optional.empty());
			testSubject.getManufacturerById(UNKNOWN_MANUFACTURER_ID);
		});
	}

	@Test
	public void getManufacturerByIdWhenTheIdBelongsToManufacturerMarkedAsDeleted() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(REMOVED_MANUFACTURER_ID)).thenReturn(Optional.empty());
			testSubject.getManufacturerById(REMOVED_MANUFACTURER_ID);
		});
	}

	@Test
	public void modifyAManufacturerThatExists() {
		Manufacturer oldManufacturer = createDefaultManufacturer();
		Manufacturer manufacturerToModify = createDefaultModifiedManufacturer();
		Mockito.when(manufacturerRepository.findById(manufacturerToModify.getId()))
				.thenReturn(Optional.of(oldManufacturer));
		Mockito.when(manufacturerRepository.save(oldManufacturer)).thenReturn(manufacturerToModify);
		assertThat(testSubject.updateManufacturer(manufacturerToModify)).as("check that the manufacturer was modified")
				.isEqualTo(manufacturerToModify);
		Mockito.verify(modelMapper).mergeEntity(manufacturerToModify, oldManufacturer);
	}

	@Test
	public void modifyAManufacturerThatNotExists() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(UNKNOWN_MANUFACTURER_ID)).thenReturn(Optional.empty());
			Manufacturer manufacturerToModify = createDefaultManufacturerWithoutId();
			manufacturerToModify.setId(UNKNOWN_MANUFACTURER_ID);
			testSubject.updateManufacturer(manufacturerToModify);
		});

	}

	@Test
	public void modifyAManufacturerMarkedAsDeleted() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(REMOVED_MANUFACTURER_ID)).thenReturn(Optional.empty());
			Manufacturer manufacturerToModify = createDefaultManufacturerWithoutId();
			manufacturerToModify.setId(REMOVED_MANUFACTURER_ID);
			testSubject.updateManufacturer(manufacturerToModify);
		});

	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToExistingManufacturer() {
		Manufacturer oldManufacturer = createDefaultManufacturer();
		Mockito.when(manufacturerRepository.findById(oldManufacturer.getId())).thenReturn(Optional.of(oldManufacturer));
		testSubject.deleteManufacturerById(oldManufacturer.getId());
		Mockito.verify(manufacturerRepository).delete(oldManufacturer);
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToNonExistingManufacturer() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(UNKNOWN_MANUFACTURER_ID)).thenReturn(Optional.empty());
			testSubject.deleteManufacturerById(UNKNOWN_MANUFACTURER_ID);
		});
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToManufacturerMarkedAsDeleted() {
		Assert.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(REMOVED_MANUFACTURER_ID)).thenReturn(Optional.empty());
			testSubject.deleteManufacturerById(REMOVED_MANUFACTURER_ID);
		});
	}

	private Page<Beer> createDefaultBeerPage() {
		return new PageImpl<Beer>(createDeafaultBeerList());
	}

	private Page<Manufacturer> createDefaultManufacturerPage() {
		return new PageImpl<Manufacturer>(List.of(createDefaultManufacturer()));
	}

	private Manufacturer createDefaultModifiedManufacturer() {
		Manufacturer manufacturer = createDefaultManufacturer();
		manufacturer.setNationality("newNationality");
		return manufacturer;
	}

	private Manufacturer createDefaultManufacturerWithoutId() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setName("manufacturerName");
		manufacturer.setNationality("nationality");
		return manufacturer;
	}

	private Manufacturer createDefaultManufacturer() {
		Manufacturer manufacturer = createDefaultManufacturerWithoutId();
		manufacturer.setId(KNOWN_MANUFACTURER_ID);
		return manufacturer;
	}

	private List<Beer> createDeafaultBeerList() {
		return Arrays.asList(new Beer());
	}
}
