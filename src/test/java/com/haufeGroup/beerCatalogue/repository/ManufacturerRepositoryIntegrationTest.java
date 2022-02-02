package com.haufeGroup.beerCatalogue.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.assertj.core.api.Fail;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.haufeGroup.beerCatalogue.exception.SortExtractorException;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql({ "/scripts/repositories/clearData.sql", "/scripts/repositories/testData.sql" })
public class ManufacturerRepositoryIntegrationTest {

	private static final long KNOWN_MANUFACTURER_ID = 1L;

	private static final long UNKNOWN_MANUFACTURER_ID = 11111L;

	private static final long REMOVED_MANUFACTURER_ID = 2;

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ManufacturerRepository testSubject;

	private static SortExtractor sortExtractor;

	@BeforeAll
	public static void initialize() {
		sortExtractor = new SortExtractor();
	}

	@Test
	public void findAllManufacturersWhenSortByDescendingId() {
		List<Manufacturer> foundedManufacturers = testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "id", "desc" }));
		assertThat(foundedManufacturers.get(0).getId())
				.as("check that the retrieved manufacturer list is sorted by descending id order").isEqualTo(4);
	}

	@Test
	public void findAllManufacturersWhenSortByAscendingNameAndDescendingId() {
		List<Manufacturer> foundedManufacturers = testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name,asc", "id,desc" }));
		assertThat(foundedManufacturers.get(0).getId())
				.as("check that the retrieved manufacturer list is sorted by ascending name and descending id")
				.isEqualTo(4);
	}

	@Test
	public void findAllManufacturersNotReturnsManufacturersMarkedAsDeleted() {
		List<Manufacturer> retrievedManufacturers = testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name,asc", "id,desc" }));
		retrievedManufacturers.forEach(retrievedBeer -> assertThat(retrievedBeer.getDeleted())
				.as("check that the retrieved manufacturer list not contains manufacturers marked as deleted")
				.isFalse());
	}

	@Test
	public void findAllManufacturersWhenSortByUnknownField() {
		Assert.assertThrows(PropertyReferenceException.class,
				() -> testSubject.findAll(sortExtractor.extractSortCriteria(new String[] { "unknownField", "desc" })));

	}

	@Test
	public void findAllManufacturersWhenSortForAFieldWithInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name", "unknownDirection" })));

	}

	@Test
	public void findAllManufacturersWhenSortForMultipleFieldsAndAtLeastOneOfThemIsUnknown() {
		Assert.assertThrows(PropertyReferenceException.class, () -> testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name, asc", "unknownField, desc" })));

	}

	@Test
	public void findAllManufacturersWhenSortForMultipleFieldsAndAtLeastOneOfThemHasADirectionNotValid() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "id, desc", "name, unknownDirection" })));

	}

	@Test
	public void getByIdAnExistingManufacturer() {
		try {
			testSubject.findById(KNOWN_MANUFACTURER_ID).orElseThrow(EntityNotFoundException::new);
		} catch (EntityNotFoundException enfe) {
			Fail.fail("manufacturer not found");
		}
	}

	@Test
	public void getByIdAnUnknownManufacturer() {
		Assert.assertThrows(EntityNotFoundException.class,
				() -> testSubject.findById(UNKNOWN_MANUFACTURER_ID).orElseThrow(EntityNotFoundException::new));
	}

	@Test
	public void getByIdAManufacturerMarkedAsDeleted() {
		Assert.assertThrows(EntityNotFoundException.class,
				() -> testSubject.findById(REMOVED_MANUFACTURER_ID).orElseThrow(EntityNotFoundException::new));
	}

	@Test
	public void addNewManufacturer() {
		Manufacturer newManufacturer = testSubject.save(createDefaultManufacturer());
		assertThat(entityManager.find(Beer.class, newManufacturer.getId()))
				.as("check that the related beer was created").isNotNull();
	}

	@Test
	public void updateManufacturer() {
		Manufacturer oldManufacturer = testSubject.findById(KNOWN_MANUFACTURER_ID)
				.orElseThrow(EntityNotFoundException::new);
		oldManufacturer.setNationality("updatedNationalty");
		Manufacturer updatedManufacturer = testSubject.save(oldManufacturer);
		assertThat(entityManager.find(Manufacturer.class, KNOWN_MANUFACTURER_ID))
				.as("check that the manufacturer was modified").isEqualTo(updatedManufacturer);
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToExistingManufacturer() {
		testSubject.deleteById(KNOWN_MANUFACTURER_ID);
		assertThat(entityManager.find(Manufacturer.class, KNOWN_MANUFACTURER_ID))
				.as("check that the related manufacturer was deleted").isNull();
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToNonExistingManufacturer() {
		Assert.assertThrows(EmptyResultDataAccessException.class,
				() -> testSubject.deleteById(UNKNOWN_MANUFACTURER_ID));
	}

	@Test
	public void deleteManufacturerByIdWhenTheIdBelongsToManufacturerMarkedAsDeleted() {
		Assert.assertThrows(EmptyResultDataAccessException.class,
				() -> testSubject.deleteById(REMOVED_MANUFACTURER_ID));
	}

	private Manufacturer createDefaultManufacturer() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setName("manufacturerName");
		manufacturer.setNationality("nationality");
		return manufacturer;
	}
}
