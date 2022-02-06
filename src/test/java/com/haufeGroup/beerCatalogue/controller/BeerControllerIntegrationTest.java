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
import com.haufeGroup.beerCatalogue.dto.BeerDto;
import com.haufeGroup.beerCatalogue.testWrappers.BeerDtoPageResponseWrapper;

@SpringBootTest(classes = BeerCatalogueApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/integrationTestData.sql" })
public class BeerControllerIntegrationTest {

	private static final long KNOWN_BEER_ID = 1;

	private static final long UNKOWN_BEER_ID = 1111111;

	private static final String INVALID_BEER_ID = "invalidBeerId";

	private static final long KNOWN_MANUFACTURER_ID = 1;

	private static final long UNKOWN_MANUFACTURER_ID = 1111111;

	private static final long REMOVED_BEER_ID = 2;

	private static final int PAGE_SIZE = 3;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port + "/beerCatalogue/api/beers/";
	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToKnownBeer() {
		ResponseEntity<BeerDto> response = restTemplate.getForEntity(getRootUrl() + KNOWN_BEER_ID, BeerDto.class);
		assertThat(response.getBody().getId()).as("check that the related beer is returned").isEqualTo(KNOWN_BEER_ID);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/removedManufacturerCase.sql" })
	public void getBeerByIdWhenIdBelongsToBeerMarkedAsDeletedInDatabase() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + REMOVED_BEER_ID, String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void getBeerByIdWhenTheIdBelongsToUnkownBeer() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + UNKOWN_BEER_ID, String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void getBeerByIdWhenTheIdIsNotValid() {
		ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + INVALID_BEER_ID, String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getBeersWithSortPagination() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl(),
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent()).as("check that a beer list is returned in the page").isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getBeersWithSortPaginationWhenTheResultIsMoreThanTheSpecifiedPageSize() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + "/?page=1&size=" + PAGE_SIZE, BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent()).as("check that a beer list is returned in the requested page")
				.isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getBeersWithSortPaginationWhenPageSizeIsSpecified() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + "/?page=1&size=" + PAGE_SIZE, BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().size())
				.as("check that the number of elements returned in the page is according to the page size")
				.isLessThanOrEqualTo(PAGE_SIZE);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getBeersWithSortPaginationWhenBothPageAndSortCriteriaAreSpecified() {
		String pageCriteria = "/?page=1&size=" + PAGE_SIZE;
		String sortCriteria = "&sort=name&sort=desc";
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate
				.getForEntity(getRootUrl() + pageCriteria + sortCriteria, BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent()).as("check that a beer list is returned in the requested page")
				.isNotEmpty();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getBeersWithSortPaginationWhenSortCriteriaIsNotProvidedThenPagesAreSortedByDescendingId() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl(),
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the beer list returned in the page is sorted by default criteria").isEqualTo(7);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/removedManufacturerCase.sql" })
	public void getBeersWithSortPaginationNotReturnsBeersMarkedAsDeletedInDatabase() {
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl(),
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the beer list returned in the page not contains beers marked as deleted in database")
				.isOne();
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getBeersWithSortPaginationWhenSortByDescendingName() {
		String sortCriteria = "?sort=name&sort=desc";
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl() + sortCriteria,
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the beer list returned in the page is sorted by descending name").isEqualTo(1);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortPaginationTestData.sql" })
	public void getBeersWithSortPaginationSortByAscendingNameAndAscendingId() {
		String sortCriteria = "?sort=name, asc&sort=id, asc";
		ResponseEntity<BeerDtoPageResponseWrapper> response = restTemplate.getForEntity(getRootUrl() + sortCriteria,
				BeerDtoPageResponseWrapper.class);
		assertThat(response.getBody().getContent().get(0).getId())
				.as("check that the beer list returned in the page is sorted by ascending name and ascending id")
				.isEqualTo(3);
	}

	@Test
	public void addANewBeerToKnownManufacturer() {
		ResponseEntity<BeerDto> response = restTemplate.postForEntity(getRootUrl(),
				createDefaultRequestBody(KNOWN_MANUFACTURER_ID), BeerDto.class);
		assertThat(response.getBody().getId()).as("check that the related beer was created").isNotNull();
	}

	@Test
	public void addANewBeerToUnknownManufacturer() {
		ResponseEntity<String> response = restTemplate.postForEntity(getRootUrl(),
				createDefaultRequestBody(UNKOWN_MANUFACTURER_ID), String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void addANewBeerWhenTheNewBeerIdIsProvided() {
		ResponseEntity<String> response = restTemplate.postForEntity(getRootUrl(), createDefaultKnownBeer(),
				String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void modifyKnownBeer() {
		HttpHeaders headers = new HttpHeaders();
		String resourceUrl = getRootUrl() + KNOWN_BEER_ID;
		HttpEntity<BeerDto> requestUpdate = new HttpEntity<>(createDefaultRequestBody(KNOWN_MANUFACTURER_ID), headers);
		ResponseEntity<BeerDto> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate,
				BeerDto.class);
		assertThat(response.getBody()).as("check that the related beer was updated")
				.isEqualTo(createDefaultKnownBeer());
	}

	@Test
	public void modifyUnkownBeer() {
		HttpHeaders headers = new HttpHeaders();
		String resourceUrl = getRootUrl() + UNKOWN_BEER_ID;
		HttpEntity<BeerDto> requestUpdate = new HttpEntity<>(createDefaultRequestBody(UNKOWN_MANUFACTURER_ID), headers);
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate,
				String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void modifyManufacturerOfExistingBeerIsNotAllowed() {
		HttpHeaders headers = new HttpHeaders();
		String resourceUrl = getRootUrl() + KNOWN_BEER_ID;
		HttpEntity<BeerDto> requestUpdate = new HttpEntity<>(createDefaultRequestBody(2L), headers);
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate,
				String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void removeBeerByIdWhenExists() {
		String resourceUrl = getRootUrl() + KNOWN_BEER_ID;
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, String.class);
		assertThat(response.getStatusCode()).as("check that the related beer was removed").isEqualTo(HttpStatus.OK);
	}

	@Test
	public void removeBeerByIdWhenNotExists() {
		String resourceUrl = getRootUrl() + UNKOWN_BEER_ID;
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void removeBeerByIdWhenIdIsNotValid() {
		String resourceUrl = getRootUrl() + INVALID_BEER_ID;
		ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, null, String.class);
		assertThat(response.getStatusCode()).as("check that an error response is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private BeerDto createDefaultRequestBody(Long manufacturerId) {
		BeerDto beerDto = new BeerDto();
		beerDto.setManufacturerId(manufacturerId);
		beerDto.setName("beerName");
		beerDto.setDescription("beerDescription");
		beerDto.setGraduation("beerGraduation");
		beerDto.setType("beerType");
		return beerDto;
	}

	private BeerDto createDefaultKnownBeer() {
		BeerDto defaultKnownBeer = createDefaultRequestBody(KNOWN_MANUFACTURER_ID);
		defaultKnownBeer.setId(KNOWN_BEER_ID);
		return defaultKnownBeer;
	}
}
