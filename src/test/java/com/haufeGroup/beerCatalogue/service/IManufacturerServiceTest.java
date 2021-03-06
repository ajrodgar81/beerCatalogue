package com.haufeGroup.beerCatalogue.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
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

import com.haufeGroup.beerCatalogue.exception.ManufacturerServiceException;
import com.haufeGroup.beerCatalogue.mapper.ManufacturerMapper;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.repository.BeerRepository;
import com.haufeGroup.beerCatalogue.repository.ManufacturerRepository;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IManufacturerServiceTest {

	private static final long KNOWN_MANUFACTURER_ID = 1;

	private static final long REMOVED_MANUFACTURER_ID = 2;

	private static final long UNKNOWN_MANUFACTURER_ID = 11111;

	private static final int PAGE_INDEX = 0;

	private static final int PAGE_SIZE = 3;

	@MockBean
	private ManufacturerRepository manufacturerRepository;

	@MockBean
	private BeerRepository beerRepository;

	@MockBean
	private ManufacturerMapper modelMapper;

	@Autowired
	private IManufacturerService testSubject;

	private static SortExtractor sortExtractor;

	@BeforeAll
	public static void initialize() {
		sortExtractor = new SortExtractor();
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
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Sort invalidSortCriteria = sortExtractor.extractSortCriteria(new String[] { "unknownName", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, invalidSortCriteria);
			Mockito.when(manufacturerRepository.findAll(sortPageable)).thenThrow(PropertyReferenceException.class);
			testSubject.getAllManufacturesWithSortPagination(sortPageable);
		});
	}

	@Test
	public void getAllManufacturersWithSortPaginationWhenTheSortPaginationCriteriaProvidedIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class,
				() -> testSubject.getAllManufacturesWithSortPagination(null));
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
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
			Mockito.when(manufacturerRepository.existsById(UNKNOWN_MANUFACTURER_ID)).thenReturn(false);
			testSubject.getManufacturerBeersWithSortPagination(UNKNOWN_MANUFACTURER_ID, sortPageable);
		});
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerIsMarkedAsDeleted() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
			Mockito.when(manufacturerRepository.existsById(REMOVED_MANUFACTURER_ID)).thenReturn(false);
			testSubject.getManufacturerBeersWithSortPagination(REMOVED_MANUFACTURER_ID, sortPageable);
		});
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExistsButTheSortCriteriaIsInvalid() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
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
	public void getManufacturerBeersWithSortPaginationWhenTheSortPaginationCriteriaProvidedIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class,
				() -> testSubject.getManufacturerBeersWithSortPagination(KNOWN_MANUFACTURER_ID, null));
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerIdProvidedIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			Sort validSortCriteria = sortExtractor.extractSortCriteria(new String[] { "name", "asc" });
			Pageable sortPageable = PageRequest.of(PAGE_INDEX, PAGE_SIZE, validSortCriteria);
			testSubject.getManufacturerBeersWithSortPagination(null, sortPageable);
		});
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenBothManufacturerIdAndSortPaginationCriteriaAreNull() {
		Assertions.assertThrows(ConstraintViolationException.class,
				() -> testSubject.getManufacturerBeersWithSortPagination(null, null));
	}

	@Test
	public void getManufacturerByIdWhenTheIdBelongsToExistingManufacturer() {
		Mockito.when(manufacturerRepository.existsById(KNOWN_MANUFACTURER_ID)).thenReturn(true);
		Mockito.when(manufacturerRepository.findById(KNOWN_MANUFACTURER_ID))
				.thenReturn(Optional.of(createDefaultManufacturer()));
		assertThat(testSubject.getManufacturerById(KNOWN_MANUFACTURER_ID)).as("check that the manufacturer is returned")
				.isEqualTo(createDefaultManufacturer());
	}

	@Test
	public void getManufacturerByIdWhenTheIdBelongsToNonExistingManufacturer() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.existsById(UNKNOWN_MANUFACTURER_ID)).thenReturn(false);
			testSubject.getManufacturerById(UNKNOWN_MANUFACTURER_ID);
		});
	}

	@Test
	public void getManufacturerByIdWhenTheIdBelongsToManufacturerMarkedAsDeleted() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.existsById(REMOVED_MANUFACTURER_ID)).thenReturn(false);
			testSubject.getManufacturerById(REMOVED_MANUFACTURER_ID);
		});
	}

	@Test
	public void getManufacturerByIdWhenTheIdIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.getManufacturerById(null));
	}

	@Test
	public void addNewManufacturerWhenTheNewManufacturerIdIsNotProvided() {
		Manufacturer newManufacturer = createDefaultManufacturerWithoutId();
		Manufacturer savedManufacturer = createDefaultManufacturer();
		Mockito.when(manufacturerRepository.save(newManufacturer)).thenReturn(savedManufacturer);
		assertThat(testSubject.addNewManufacturer(newManufacturer)).as("check that the manufacturer was crreated")
				.isEqualTo(savedManufacturer);
	}

	@Test
	public void addNewManufacturerWhenTheNewManufacturerIdIsProvided() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.existsById(KNOWN_MANUFACTURER_ID)).thenReturn(true);
			testSubject.addNewManufacturer(createDefaultManufacturer());
		});
	}

	@Test
	public void addNewManufacturerWhenTheNewManufacturerIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.addNewManufacturer(null));
	}

	@Test
	public void addNewManufacturerWhenTheIdProvidedNotBelongs() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.addNewManufacturer(null));
	}

	@Test
	public void updateAManufacturerThatExists() {
		Manufacturer oldManufacturer = createDefaultManufacturer();
		Manufacturer manufacturerToModify = createDefaultModifiedManufacturer();
		Mockito.when(manufacturerRepository.findById(manufacturerToModify.getId()))
				.thenReturn(Optional.of(oldManufacturer));
		Mockito.when(manufacturerRepository.save(oldManufacturer)).thenReturn(manufacturerToModify);
		assertThat(testSubject.updateManufacturer(manufacturerToModify)).as("check that the manufacturer was updated")
				.isEqualTo(manufacturerToModify);
		Mockito.verify(modelMapper).mergeEntity(manufacturerToModify, oldManufacturer);
	}

	@Test
	public void updateAManufacturerThatNotExists() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(UNKNOWN_MANUFACTURER_ID))
					.thenThrow(NoSuchElementException.class);
			Manufacturer manufacturerToModify = createDefaultManufacturerWithoutId();
			manufacturerToModify.setId(UNKNOWN_MANUFACTURER_ID);
			testSubject.updateManufacturer(manufacturerToModify);
		});

	}

	@Test
	public void updateAManufacturerMarkedAsDeleted() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.findById(REMOVED_MANUFACTURER_ID))
					.thenThrow(NoSuchElementException.class);
			Manufacturer manufacturerToModify = createDefaultManufacturerWithoutId();
			manufacturerToModify.setId(REMOVED_MANUFACTURER_ID);
			testSubject.updateManufacturer(manufacturerToModify);
		});

	}

	@Test
	public void updateManufactuerWhenTheManufacturerToModifyIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.updateManufacturer(null));
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToExistingManufacturer() {
		Mockito.when(manufacturerRepository.existsById(KNOWN_MANUFACTURER_ID)).thenReturn(true);
		testSubject.deleteManufacturerById(KNOWN_MANUFACTURER_ID);
		Mockito.verify(manufacturerRepository).deleteById(KNOWN_MANUFACTURER_ID);
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToNonExistingManufacturer() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.existsById(UNKNOWN_MANUFACTURER_ID)).thenReturn(false);
			testSubject.deleteManufacturerById(UNKNOWN_MANUFACTURER_ID);
		});
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToManufacturerMarkedAsDeleted() {
		Assertions.assertThrows(ManufacturerServiceException.class, () -> {
			Mockito.when(manufacturerRepository.existsById(REMOVED_MANUFACTURER_ID)).thenReturn(false);
			testSubject.deleteManufacturerById(REMOVED_MANUFACTURER_ID);
		});
	}

	@Test
	public void deleteManufactuerByIdWhenTheIdIsNull() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> testSubject.deleteManufacturerById(null));
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
