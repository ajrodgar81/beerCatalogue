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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class BeerRepositoryIntegrationTest {

	private static final long KNOWN_BEER_ID = 1;

	private static final long REMOVED_BEER_ID = 2;

	private static final long UNKNOWN_BEER_ID = 11111;

	private static final long KNOWN_MANUFACTURER_ID = 1;

	private static final long DELETED_MANUFACTURER_ID = 2;

	private static final long UNKNOWN_MANUFACTURER_ID = 11111;

	private static final int PAGE_INDEX = 0;

	private static final int ELEMENT_PER_PAGE = 3;

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private BeerRepository testSubject;

	private static SortExtractor sortExtractor;

	@BeforeAll
	public static void initialize() {
		sortExtractor = new SortExtractor();
	}

	@Test
	public void findAllBeersWithPaginationCriteriaSortByDescendingId() {
		Page<Beer> beerPage = testSubject.findAll(getPageableAccordingToSortCriteria(new String[] { "id", "desc" }));
		assertThat(beerPage.getContent().get(0).getId())
				.as("check that the retrieved page is sorted by descending beer id order").isEqualTo(7);
	}

	@Test
	public void findAllBeersWithPaginationCriteriaSortByAscendingNameAndDescendingName() {
		Page<Beer> beerPage = testSubject
				.findAll(getPageableAccordingToSortCriteria(new String[] { "name,asc", "id,desc" }));
		assertThat(beerPage.getContent().get(0).getId())
				.as("check that the retrieved beer page is sorted by ascending name and descending id").isEqualTo(6);
	}

	@Test
	public void findAllBeersWithPaginationCriteriaNotReturnsBeersMarkedAsDeleted() {
		Page<Beer> beerPage = testSubject
				.findAll(getPageableAccordingToSortCriteria(new String[] { "name,asc", "id,desc" }));
		beerPage.forEach(retrievedBeer -> assertThat(retrievedBeer.getDeleted())
				.as("check that the retrieved beer page not contains beers marked as deleted").isFalse());
	}

	@Test
	public void findAllBeersWithPaginationCriteriaSortByUnknownField() {
		Assert.assertThrows(PropertyReferenceException.class,
				() -> testSubject.findAll(getPageableAccordingToSortCriteria(new String[] { "unknownField", "desc" })));

	}

	@Test
	public void findAllBeersWithPaginationCriteriaSortForAFieldWithInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject
				.findAll(getPageableAccordingToSortCriteria(new String[] { "name", "unknownDirection" })));

	}

	@Test
	public void findAllBeersWithPaginationCriteriaSortForMultipleFieldsAndAtLeastOneOfThemIsUnknown() {
		Assert.assertThrows(PropertyReferenceException.class, () -> testSubject
				.findAll(getPageableAccordingToSortCriteria(new String[] { "name, asc", "unknownField, desc" })));

	}

	@Test
	public void findAllBeersWhenSortByDescendingId() {
		List<Beer> foundedBeers = testSubject.findAll(sortExtractor.extractSortCriteria(new String[] { "id", "desc" }));
		assertThat(foundedBeers.get(0).getId())
				.as("check that the retrieved beer list is sorted by descending id order").isEqualTo(7);
	}

	@Test
	public void findAllBeersWhenSortByAscendingNameAndDescendingName() {
		List<Beer> foundedBeers = testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name,asc", "id,desc" }));
		assertThat(foundedBeers.get(0).getId())
				.as("check that the retrieved beer list is sorted by ascending name and descending id").isEqualTo(6);
	}

	@Test
	public void findAllBeersNotReturnsBeersMarkedAsDeleted() {
		List<Beer> retrievedBeers = testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name,asc", "id,desc" }));
		retrievedBeers.forEach(retrievedBeer -> assertThat(retrievedBeer.getDeleted())
				.as("check that the retrieved beer list not contains beers marked as deleted").isFalse());
	}

	@Test
	public void findAllBeersWhenSortByUnknownField() {
		Assert.assertThrows(PropertyReferenceException.class,
				() -> testSubject.findAll(sortExtractor.extractSortCriteria(new String[] { "unknownField", "desc" })));

	}

	@Test
	public void findAllBeersWhenSortForAFieldWithInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name", "unknownDirection" })));

	}

	@Test
	public void findAllBeersWhenSortForMultipleFieldsAndAtLeastOneOfThemIsUnknown() {
		Assert.assertThrows(PropertyReferenceException.class, () -> testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "name, asc", "unknownField, desc" })));

	}

	@Test
	public void findAllBeersWhenSortForMultipleFieldsAndAtLeastOneOfThemHasADirectionNotValid() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject
				.findAll(sortExtractor.extractSortCriteria(new String[] { "id, desc", "name, unknownDirection" })));

	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaSortByDescendingBeerId() {
		Page<Beer> beerPage = testSubject.findByManufacturerId(1L,
				getPageableAccordingToSortCriteria(new String[] { "id", "desc" }));
		assertThat(beerPage.getContent().get(0).getId())
				.as("check that the retrieved beer page is sorted by descending beer id").isEqualTo(7);
	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaSortByAscendingBeerNameAndDescendingBeerId() {
		Page<Beer> beerPage = testSubject.findByManufacturerId(1L,
				getPageableAccordingToSortCriteria(new String[] { "name,asc", "id,desc" }));
		assertThat(beerPage.getContent().get(0).getId())
				.as("check that the retrieved beer page is sorted by ascending beer name and descending beer id")
				.isEqualTo(6);
	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaSortByUnknownField() {
		Assert.assertThrows(PropertyReferenceException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "unknownField", "desc" })));

	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaSortForAFieldWithInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "name", "unknownDirection" })));

	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaSortForMultipleFieldsAndAtLeastOneOfThemIsUnknown() {
		Assert.assertThrows(PropertyReferenceException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "name, asc", "unknownField, desc" })));
	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaSortForMultipleFieldsAndAtLeastOneOfThemWithInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "id, desc", "name, unknownDirection" })));
	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaTheRelatedManufacturerIsUnkwown() {
		Page<Beer> retrievedBeerList = testSubject.findByManufacturerId(UNKNOWN_MANUFACTURER_ID,
				getPageableAccordingToSortCriteria(new String[] { "id, desc", "name, asc" }));
		assertThat(retrievedBeerList).as("check that an empty beer page is returned").isEmpty();

	}

	@Test
	public void findByManufacturerIdWithPaginationCriteriaTheRelatedManufacturerIsMarkedAsDeleted() {
		Page<Beer> retrievedBeerList = testSubject.findByManufacturerId(DELETED_MANUFACTURER_ID,
				getPageableAccordingToSortCriteria(new String[] { "id, desc", "name, asc" }));
		assertThat(retrievedBeerList).as("check that an empty beer page is returned").isEmpty();

	}

	@Test
	public void findByManufacturerIdSortByDescendingBeerId() {
		List<Beer> foundedBeers = testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "id", "desc" }));
		assertThat(foundedBeers.get(0).getId()).as("check that the retrieved beer list is sorted by its descending id")
				.isEqualTo(7);
	}

	@Test
	public void findByManufacturerIdWhenSortByAscendingBeerNameAndDescendingBeerId() {
		List<Beer> foundedBeers = testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "name,asc", "id,desc" }));
		assertThat(foundedBeers.get(0).getId())
				.as("check that the retrieved beer list is sorted by its ascending name and its descending id")
				.isEqualTo(6);
	}

	@Test
	public void findByManufacturerIdWhenSortByUnknownField() {
		Assert.assertThrows(PropertyReferenceException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "unknownField", "desc" })));

	}

	@Test
	public void findByManufacturerIdWhenSortForAFieldWithInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "name", "unknownDirection" })));

	}

	@Test
	public void findByManufacturerIdWhenSortForMultipleFieldsAndAtLeastOneOfThemIsUnknown() {
		Assert.assertThrows(PropertyReferenceException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "name, asc", "unknownField, desc" })));
	}

	@Test
	public void findByManufacturerIdWhenSortForMultipleFieldsAndAtLeastOneOfThemWithInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class, () -> testSubject.findByManufacturerId(1L,
				sortExtractor.extractSortCriteria(new String[] { "id, desc", "name, unknownDirection" })));
	}

	@Test
	public void findByManufacturerIdWhenTheRelatedManufacturerIsUnkwown() {
		List<Beer> retrievedBeerList = testSubject.findByManufacturerId(UNKNOWN_MANUFACTURER_ID,
				sortExtractor.extractSortCriteria(new String[] { "id, desc", "name, asc" }));
		assertThat(retrievedBeerList).as("check that an empty beer list is returned").isEmpty();

	}

	@Test
	public void findByManufacturerIdWhenTheRelatedManufacturerIsMarkedAsDeleted() {
		List<Beer> retrievedBeerList = testSubject.findByManufacturerId(DELETED_MANUFACTURER_ID,
				sortExtractor.extractSortCriteria(new String[] { "id, desc", "name, asc" }));
		assertThat(retrievedBeerList).as("check that an empty beer list is returned").isEmpty();

	}

	@Test
	public void addNewBeerWhenTheRelatedManufacturerExists() {
		Beer newBeer = testSubject.save(createDefaultBeer());
		assertThat(entityManager.find(Beer.class, newBeer.getId())).as("check that the related beer was created")
				.isNotNull();
	}

	@Test
	public void addNewBeerWhenTheRelatedManufacturerIsUnknown() {
		Assert.assertThrows(DataIntegrityViolationException.class, () -> {
			Beer beer = createDefaultBeerWithoutManufacturer();
			beer.setManufacturer(new Manufacturer());
			beer.getManufacturer().setId(UNKNOWN_MANUFACTURER_ID);
			testSubject.save(beer);
		});

	}

	@Test
	public void addNewBeerWhenManufacturerIsNull() {
		Assert.assertThrows(DataIntegrityViolationException.class,
				() -> testSubject.save(createDefaultBeerWithoutManufacturer()));

	}

	@Test
	public void getByIdAnExistingBeer() {
		try {
			testSubject.findById(KNOWN_BEER_ID).orElseThrow(EntityNotFoundException::new);
		} catch (EntityNotFoundException enfe) {
			Fail.fail("beer not found");
		}
	}

	@Test
	public void getByIdAnUnknownBeer() {
		Assert.assertThrows(EntityNotFoundException.class,
				() -> testSubject.findById(UNKNOWN_BEER_ID).orElseThrow(EntityNotFoundException::new));
	}

	@Test
	public void getByIdBeerMarkedAsDeleted() {
		Assert.assertThrows(EntityNotFoundException.class,
				() -> testSubject.findById(REMOVED_BEER_ID).orElseThrow(EntityNotFoundException::new));
	}

	@Test
	public void updateBeer() {
		Beer oldBeer = testSubject.findById(KNOWN_BEER_ID).orElseThrow(EntityNotFoundException::new);
		oldBeer.setDescription("updatedDescription");
		Beer updatedBeer = testSubject.save(oldBeer);
		assertThat(entityManager.find(Beer.class, updatedBeer.getId())).as("check that the beer was modified")
				.isEqualTo(updatedBeer);
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToExistingBeer() {
		testSubject.deleteById(KNOWN_BEER_ID);
		assertThat(entityManager.find(Beer.class, 1L)).as("check that the related beer was deleted").isNull();
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToBeerMarkedAsDeleted() {
		Assert.assertThrows(EmptyResultDataAccessException.class, () -> testSubject.deleteById(REMOVED_BEER_ID));
	}

	@Test
	public void deleteBeerByIdWhenTheIdBelongsToNonExistingBeer() {
		Assert.assertThrows(EmptyResultDataAccessException.class, () -> testSubject.deleteById(UNKNOWN_BEER_ID));
	}

	private Pageable getPageableAccordingToSortCriteria(final String[] sortCriteria) {
		return PageRequest.of(PAGE_INDEX, ELEMENT_PER_PAGE, sortExtractor.extractSortCriteria(sortCriteria));
	}

	private Beer createDefaultBeer() {
		Beer beer = createDefaultBeerWithoutManufacturer();
		beer.setManufacturer(entityManager.find(Manufacturer.class, KNOWN_MANUFACTURER_ID));
		return beer;
	}

	private Beer createDefaultBeerWithoutManufacturer() {
		Beer beer = new Beer();
		beer.setName("beerName");
		beer.setGraduation("graduation");
		beer.setDescription("description");
		beer.setType("beerType");
		return beer;
	}
}
