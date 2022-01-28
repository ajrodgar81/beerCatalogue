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
import com.haufeGroup.beerCatalogue.model.Manufacturer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BeerCatalogueApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManufacturerControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port + "/api/manufacturers/";
	}

	@Test
	public void testGetManufacturers() {
		ResponseEntity<Manufacturer[]> response = restTemplate.getForEntity(getRootUrl(), Manufacturer[].class);
		assertThat(response.getBody().length).as("check that a manufacturer is returned").isEqualTo(1);
	}

	@Test
	public void testGetAManufacturerById() {
		Long manufacturerId = 1L;
		ResponseEntity<Manufacturer> response = restTemplate.getForEntity(getRootUrl() + manufacturerId.toString(),
				Manufacturer.class);
		assertThat(response.getBody().getId()).as("check that a manufacturer is returned").isEqualTo(manufacturerId);
	}

	@Test
	public void testGetBeerList() {
		Long manufacturerId = 1L;
		ResponseEntity<Beer[]> response = restTemplate
				.getForEntity(getRootUrl() + manufacturerId.toString() + "/beerList", Beer[].class);
		assertThat(response.getBody().length).as("check that the related beer list is returned").isEqualTo(1);
	}

	@Test
	public void testAddNewManufacturer() {
		Long manufacturerId = 1L;
		ResponseEntity<Manufacturer> response = restTemplate.postForEntity(getRootUrl(),
				createDefaultManufacturer(null), Manufacturer.class);
		assertThat(response.getBody().getId()).as("check that a manufacturer was created").isEqualTo(manufacturerId);
	}

	@Test
	public void testModifyAManufacturer() {
		HttpHeaders headers = new HttpHeaders();
		Long manufacturerId = 1L;
		String resourceUrl = getRootUrl() + manufacturerId;
		HttpEntity<Manufacturer> requestUpdate = new HttpEntity<>(createDefaultManufacturer(null), headers);
		ResponseEntity<Manufacturer> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, requestUpdate,
				Manufacturer.class);
		assertThat(response.getBody()).as("check that a manufacturer was updated")
				.isEqualTo(createDefaultManufacturer(manufacturerId));
	}

	@Test
	public void testDeleteAManufacturer() {
		HttpHeaders headers = new HttpHeaders();
		Long manufacturerId = 1L;
		String resourceUrl = getRootUrl() + manufacturerId;
		HttpEntity<Manufacturer> requestUpdate = new HttpEntity<>(createDefaultManufacturer(manufacturerId), headers);
		ResponseEntity<Manufacturer> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, requestUpdate,
				Manufacturer.class);
		assertThat(response.getBody()).as("check that a manufacturer was removed").isNull();
	}

	private Manufacturer createDefaultManufacturer(final Long id) {
		return Manufacturer.builder().id(id).name("manufacturerName").nationality("nationality").build();
	}
}
