package com.haufeGroup.beerCatalogue.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import com.haufeGroup.beerCatalogue.BeerCatalogueApplication;
import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;
import com.haufeGroup.beerCatalogue.testWrappers.BeerDtoPageResponseWrapper;
import com.haufeGroup.beerCatalogue.testWrappers.ManufacturerDtoPageResponseWrapper;

@SpringBootTest(classes = BeerCatalogueApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/integrationTestData.sql" })
public class ManufacturerControllerIntegrationTest {

	private static final long KNOWN_MANUFACTURER_ID = 1;
	private static final long UNKOWN_MANUFACTURER_ID = 1111111;
	private static final String INVALID_MANUFACTURER_ID = "invalidManufacturerId";
	private static final long REMOVED_MANUFACTURER_ID = 1;
	private static final int PAGE_SIZE = 2;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port + "/api/manufacturers/";
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturersWithSortPagination() {
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl(),
				ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent()).as("check that a manufacturer list is returned in the page")
				.isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturersWithSortPaginationWhenTheResultIsMoreThanTheSpecifiedPageSize() {
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + "/?page=1&size=" + PAGE_SIZE, ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent())
				.as("check that a manufacturer list is returned in the requested page").isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturersWithSortPaginationWhenPageSizeIsSpecified() {
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + "/?page=1&size=" + PAGE_SIZE, ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().size())
				.as("check that the number of elements returned in the page is according to the page size")
				.isLessThanOrEqualTo(PAGE_SIZE);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturersWithSortPaginationWhenBothPageAndSortCriteriaAreSpecified() {
		String pageCriteria = "/?page=1&size=" + PAGE_SIZE;
		String sortCriteria = "&sort=name&sort=desc";
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + pageCriteria + sortCriteria, ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent())
				.as("check that a manufacturer list is returned in the requested page").isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturersWithSortPaginationWhenSortCriteriaIsNotProvidedThenPagesAreSortedByDescendingId() {
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl(),
				ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the manufacturer list returned in the page is sorted by default criteria").isEqualTo(4);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/removedManufacturerCase.sql" })
	public void getManufacturersWithSortPaginationNotReturnsManufacturersMarkedAsDeletedInDatabase() {
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl(),
				ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().size()).as(
				"check that the manufacturer list returned in the page not contains manufacturers marked as deleted in database")
				.isOne();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturersWithSortPaginationWhenSortByDescendingName() {
		String sortCriteria = "?sort=name&sort=desc";
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + sortCriteria, ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the manufacturer list returned in the page is sorted by descending name").isEqualTo(1);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturersWithSortPaginationSortByAscendingNameAndAscendingId() {
		String sortCriteria = "?sort=name, asc&sort=id, asc";
		ResponseEntity<ManufacturerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + sortCriteria, ManufacturerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId()).as(
				"check that the manufacturer list returned in the page is sorted by ascending name and ascending id")
				.isEqualTo(2);
	}

	@Test
	public void getManufacturerByIdWhenTheIdBelongsToKnownManufacturer() {
		ResponseEntity<ManufacturerDto> response = restTemplate.getForEntity(getRootUrl() + KNOWN_MANUFACTURER_ID,
				ManufacturerDto.class);
		assertThat(response.getBody().getId()).as("check that the related manufacturer is returned")
				.isEqualTo(KNOWN_MANUFACTURER_ID);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/removedManufacturerCase.sql" })
	public void getManufacturerByIdWhenIdBelongsToManufacturerMarkedAsDeletedInDatabase() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + REMOVED_MANUFACTURER_ID,
				String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void getManufacturerByIdWhenTheTheIdBelongsToUnkownManufacturer() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + UNKOWN_MANUFACTURER_ID,
				String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void getManufacturerByIdWhenTheIdIsNotValid() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + INVALID_MANUFACTURER_ID,
				String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturerBeersWithSortPaginationWhenManufacturerExistsAndTheResultIsMoreThanTheSpecifiedPageSize() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(
				getRootUrl() + KNOWN_MANUFACTURER_ID + "/beers/" + "/?page=1&size=" + PAGE_SIZE,
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent()).as("check that a beer list is returned in the requested page")
				.isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturerBeersWithSortPaginationWhenManufacturerExistsAndPageSizeIsSpecified() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(
				getRootUrl() + KNOWN_MANUFACTURER_ID + "/beers/" + "/?page=1&size=" + PAGE_SIZE,
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().size())
				.as("check that the number of elements returned in the page is according to the page size")
				.isLessThanOrEqualTo(PAGE_SIZE);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturerBeersWithSortPaginationWhenManufacturerExistsAndBothPageAndSortCriteriaAreSpecified() {
		String pageCriteria = "/?page=1&size=" + PAGE_SIZE;
		String sortCriteria = "&sort=name&sort=desc";
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(
				getRootUrl() + KNOWN_MANUFACTURER_ID + "/beers/" + pageCriteria + sortCriteria,
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent()).as("check that a beer list is returned in the requested page")
				.isNotEmpty();
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExists() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + KNOWN_MANUFACTURER_ID + "/beers/", BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent()).as("check that a beer list is returned in the requested paget")
				.isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExistsAndASortIsNotProvidedThenTheRelatedPageIsSortByDescendingId() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + KNOWN_MANUFACTURER_ID + "/beers/", BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the beer list returned in the page is sorted by default criteria").isEqualTo(7);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExistsAndSortByAscendingNameAndAscendingId() {
		String sortCriteria = "?sort=name, asc&sort=id, asc";
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(
				getRootUrl() + KNOWN_MANUFACTURER_ID + "/beers/" + sortCriteria, BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the beer list returned in the page is sort by ascending name and ascending id")
				.isEqualTo(3);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerExistsAndSortByAscendingId() {
		String sortCriteria = "?sort=id&sort=asc";
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(
				getRootUrl() + KNOWN_MANUFACTURER_ID + "/beers/" + sortCriteria, BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the beer list returned in the page is sort by ascending id").isEqualTo(1);
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerNotExists() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + UNKOWN_MANUFACTURER_ID + "/beers/",
				String.class);
		assertThat(response.getStatusCode()).as("check that a bad request status code is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/removedManufacturerCase.sql" })
	public void getManufacturerBeersWithSortPaginationWhenWhenManufacturerIdBelongsToManufacturerMarkedAsDeletedInDatabase() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + REMOVED_MANUFACTURER_ID + "/beers/",
				String.class);
		assertThat(response.getStatusCode()).as("check that a bad request status code is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void getManufacturerBeersWithSortPaginationWhenTheManufacturerIdIsNotValid() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + "invalidManufacturerId" + "/beers/",
				String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void addNewManufacturer() {
		ResponseEntity<ManufacturerDto> response = restTemplate.postForEntity(getRootUrl(), createDefaultBody(),
				ManufacturerDto.class);
		assertThat(response.getBody().getId()).as("check that the related manufacturer was created").isNotNull();
	}

	@Test
	public void moodifyKnownManufacturer() {
		HttpHeaders headers = new HttpHeaders();
		String resourceUrl = getRootUrl() + KNOWN_MANUFACTURER_ID;
		HttpEntity<ManufacturerDto> requestUpdate = new HttpEntity<>(createDefaultBody(), headers);
		ResponseEntity<ManufacturerDto> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate,
				ManufacturerDto.class);
		assertThat(response.getBody()).as("check that the related manufacturer was updated")
				.isEqualTo(createDefaultManufacturerWithId(KNOWN_MANUFACTURER_ID));
	}

	@Test
	public void moodifyUnknownManufacturer() {
		HttpHeaders headers = new HttpHeaders();
		String resourceUrl = getRootUrl() + UNKOWN_MANUFACTURER_ID;
		HttpEntity<ManufacturerDto> requestUpdate = new HttpEntity<>(createDefaultBody(), headers);
		ResponseEntity<ManufacturerDto> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate,
				ManufacturerDto.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void removeManufacturerByIdWhenExists() {
		String resourceUrl = getRootUrl() + KNOWN_MANUFACTURER_ID;
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, String.class);
		assertThat(response.getStatusCode()).as("check that a manufacturer was removed").isEqualTo(HttpStatus.OK);
	}

	@Test
	public void removeManufacturerByIdWhenNotExists() {
		String resourceUrl = getRootUrl() + UNKOWN_MANUFACTURER_ID;
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void removeManufacturerByIdWhenIdIsNotValid() {
		String resourceUrl = getRootUrl() + INVALID_MANUFACTURER_ID;
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private ManufacturerDto createDefaultManufacturerWithId(final Long id) {
		ManufacturerDto manufacturerDto = new ManufacturerDto();
		manufacturerDto.setId(id);
		manufacturerDto.setName("manufacturerName");
		manufacturerDto.setNationality("nationality");
		return manufacturerDto;
	}

	private ManufacturerDto createDefaultBody() {
		return createDefaultManufacturerWithId(null);
	}
}
