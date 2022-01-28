package com.haufeGroup.beerCatalogue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;
import com.haufeGroup.beerCatalogue.service.IManufacturerService;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerController {

	@Autowired
	private IManufacturerService manufacturerService;

	@GetMapping("/{id}")
	public Manufacturer getManufacturer(@PathVariable Long id) {
		return manufacturerService.getManufacturerById(id);
	}

	@GetMapping("/{id}/beerList")
	public List<Beer> getBeerList(@PathVariable(name = "id") Long manufacturerId) {
		return manufacturerService.getBeerList(manufacturerId);
	}

	@GetMapping()
	public List<Manufacturer> getManufacturers() {
		return manufacturerService.getManufacturers();
	}

	@PostMapping()
	public Manufacturer addManufacturer(@RequestBody final Manufacturer newManufacturer) {
		return manufacturerService.addManufacturer(newManufacturer);
	}

	@PutMapping("/{id}")
	public Manufacturer modifyManufacturer(@PathVariable Long id, @RequestBody final Manufacturer updatedManufacturer) {
		updatedManufacturer.setId(id);
		return manufacturerService.updateManufacturer(updatedManufacturer);
	}

	@DeleteMapping("/{id}")
	public void removeManufacturer(@PathVariable final Long id) {
		manufacturerService.deleteManufacturer(id);
	}

}
