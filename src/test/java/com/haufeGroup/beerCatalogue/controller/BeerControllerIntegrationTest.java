package com.haufeGroup.beerCatalogue.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.haufeGroup.beerCatalogue.BeerCatalogueApplication;
import com.haufeGroup.beerCatalogue.model.Beer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BeerCatalogueApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port + "/api/beers/";
	}

	@Test
	public void testGetABeerById() {
		Long beerId = 1L;
		ResponseEntity<Beer> response = restTemplate.getForEntity(getRootUrl() + beerId.toString(), Beer.class);
		assertThat(response.getBody().getId()).as("check that a beer is returned").isEqualTo(beerId);
	}

	@Test
	public void testAddNewBeer() {
		Long mockedBeerId = 1L;
		ResponseEntity<Beer> response = restTemplate.postForEntity(getRootUrl(), createDefaultBeer(null), Beer.class);
		assertThat(response.getBody().getId()).as("check that a beer was created").isEqualTo(mockedBeerId);
	}

	@Test
	public void testModifyABeer() {
		HttpHeaders headers = new HttpHeaders();
		Beer updatedBeer = createDefaultBeer(1L);
		String resourceUrl = getRootUrl() + updatedBeer.getId();
		HttpEntity<Beer> requestUpdate = new HttpEntity<>(createDefaultBeer(null), headers);
		ResponseEntity<Beer> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate, Beer.class);
		assertThat(response.getBody()).as("check that a beer was updated").isEqualTo(updatedBeer);
	}

	@Test
	public void testDeleteABeer() {
		HttpHeaders headers = new HttpHeaders();
		Long beerId = 1L;
		String resourceUrl = getRootUrl() + beerId;
		HttpEntity<Beer> requestUpdate = new HttpEntity<>(createDefaultBeer(null), headers);
		ResponseEntity<Beer> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, requestUpdate,
				Beer.class);
		assertThat(response.getBody()).as("check that a beer was removed").isNull();
	}

	private Beer createDefaultBeer(final Long id) {
		return Beer.builder().id(id).beerName("beerName").description("beerDescription").graduation("gradutation")
				.manufacturerId(1L).build();
	}
}
