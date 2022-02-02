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
import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;

@SpringBootTest(classes = BeerCatalogueApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/integrationTestData.sql" })
public class ManufacturerControllerIntegrationTest {

	private static final long KNOWN_MANUFACTURER_ID = 1;
	private static final long UNKOWN_MANUFACTURER_ID = 1111111;
	private static final String INVALID_MANUFACTURER_ID = "invalidManufacturerId";
	private static final long REMOVED_MANUFACTURER_ID = 1;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port + "/api/manufacturers/";
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortListTestData.sql" })
	public void getManufacturerList() {
		ResponseEntity<ManufacturerDto[]> response = restTemplate.getForEntity(getRootUrl(), ManufacturerDto[].class);
		assertThat(response.getBody().length).as("check that the manufacturer list is returned").isGreaterThan(0);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortListTestData.sql" })
	public void getManufacturerListWhenSortIsNotProvidedThenTheRelatedListIsSortedByDescendingId() {
		ResponseEntity<ManufacturerDto[]> response = restTemplate.getForEntity(getRootUrl(), ManufacturerDto[].class);
		assertThat(response.getBody()[0].getId())
				.as("check that the returned manufacturer list is sort by default criteria").isEqualTo(4);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortListTestData.sql" })
	public void getManufacturerListSortByAscendingId() {
		String sortCriteria = "?sort=id&sort=asc";
		ResponseEntity<ManufacturerDto[]> response = restTemplate.getForEntity(getRootUrl() + sortCriteria,
				ManufacturerDto[].class);
		assertThat(response.getBody()[0].getId())
				.as("check that the returned manufacturer list is sort by ascending id").isEqualTo(1);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortListTestData.sql" })
	public void getManufacturerListSortByAscendingNameAndDescendingId() {
		String sortCriteria = "?sort=name,asc&sort=id,desc";
		ResponseEntity<ManufacturerDto[]> response = restTemplate.getForEntity(getRootUrl() + sortCriteria,
				ManufacturerDto[].class);
		assertThat(response.getBody()[0].getId())
				.as("check that the returned manufacturer list is sort by ascending name and descending id")
				.isEqualTo(4);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/removedManufacturerCase.sql" })
	public void getManufacturerListNotReturnsManufacturersMarkedAsDeletedInDatabase() {
		ResponseEntity<ManufacturerDto[]> response = restTemplate.getForEntity(getRootUrl(), ManufacturerDto[].class);
		assertThat(response.getBody().length)
				.as("check that the returned manufacturer list not contains manufacturers marked as deleted in database").isOne();
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
	public void getBeerListWhenTheManufacturerExists() {
		ResponseEntity<BeerDto[]> response = restTemplate
				.getForEntity(getRootUrl() + KNOWN_MANUFACTURER_ID + "/beerList", BeerDto[].class);
		assertThat(response.getBody().length).as("check that the manufacturer beer list is returned").isGreaterThan(0);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortListTestData.sql" })
	public void getBeerListWhenTheManufacturerExistsAndASortIsNotProvidedThenTheRelatedListIsSortByDescendingId() {
		ResponseEntity<BeerDto[]> response = restTemplate
				.getForEntity(getRootUrl() + KNOWN_MANUFACTURER_ID + "/beerList", BeerDto[].class);
		assertThat(response.getBody()[0].getId()).as("check that the returned beer list is sorted by default criteria")
				.isEqualTo(7);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortListTestData.sql" })
	public void getBeerListSortByAscendingNameAndAscendingIdWhenTheManufacturerExists() {
		String sortCriteria = "?sort=name, asc&sort=id, asc";
		ResponseEntity<BeerDto[]> response = restTemplate
				.getForEntity(getRootUrl() + KNOWN_MANUFACTURER_ID + "/beerList/" + sortCriteria, BeerDto[].class);
		assertThat(response.getBody()[0].getId())
				.as("check that the returned beer list is sort by ascending name and ascending id").isEqualTo(3);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/sortListTestData.sql" })
	public void getBeerListSortByAscendingIdWhenTheManufacturerExists() {
		String sortCriteria = "?sort=id&sort=asc";
		ResponseEntity<BeerDto[]> response = restTemplate
				.getForEntity(getRootUrl() + KNOWN_MANUFACTURER_ID + "/beerList/" + sortCriteria, BeerDto[].class);
		assertThat(response.getBody()[0].getId()).as("check that the returned beer list is sort by ascending id")
				.isEqualTo(1);
	}

	@Test
	public void getBeerListWhenTheManufacturerNotExists() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(getRootUrl() + UNKOWN_MANUFACTURER_ID + "/beerList/", String.class);
		assertThat(response.getStatusCode()).as("check that a bad request status code is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@Sql({ "/scripts/controllers/clearData.sql", "/scripts/controllers/removedManufacturerCase.sql" })
	public void getBeerListWhenWhenManufacturerIdBelongsToManufacturerMarkedAsDeletedInDatabase() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(getRootUrl() + REMOVED_MANUFACTURER_ID + "/beerList/", String.class);
		assertThat(response.getStatusCode()).as("check that a bad request status code is returned")
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void getBeerListWhenTheManufacturerIdIsNotValid() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(getRootUrl() + "invalidManufacturerId" + "/beerList/", String.class);
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
