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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haufeGroup.beerCatalogue.dto.BeerDto;
import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;
import com.haufeGroup.beerCatalogue.mapper.BeerMapper;
import com.haufeGroup.beerCatalogue.mapper.ManufacturerMapper;
import com.haufeGroup.beerCatalogue.service.IManufacturerService;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerController {

	@Autowired
	private IManufacturerService manufacturerService;

	@Autowired
	ManufacturerMapper manufacturerMapper;

	@Autowired
	BeerMapper beerMapper;

	@Autowired
	SortExtractor sortExtractor;

	@GetMapping("/")
	public List<ManufacturerDto> getManufacturerListSortByUserCriteria(
			@RequestParam(defaultValue = "id,desc") String[] sort) {
		return manufacturerMapper.mapFromEntityList(
				manufacturerService.getManufacturerListSortByCriteria(sortExtractor.extractSortCriteria(sort)));
	}

	@GetMapping("/{id}")
	public ManufacturerDto getManufacturer(@PathVariable Long id) {
		return manufacturerMapper.mapFromEntity(manufacturerService.getManufacturerById(id));
	}

	@GetMapping("/{id}/beerList")
	public List<BeerDto> getBeerListSortByCriteria(@PathVariable(name = "id") Long manufacturerId,
			@RequestParam(defaultValue = "id,desc") String[] sort) {
		return beerMapper.mapFromEntityList(manufacturerService
				.getBeerListSortByCriteria(manufacturerId, sortExtractor.extractSortCriteria(sort)));
	}

	@PostMapping("/")
	public ManufacturerDto addManufacturer(@RequestBody final ManufacturerDto newManufacturerDto) {
		return manufacturerMapper
				.mapFromEntity(manufacturerService.addManufacturer(manufacturerMapper.mapFromDto(newManufacturerDto)));
	}

	@PutMapping("/{id}")
	public ManufacturerDto modifyManufacturer(@PathVariable Long id,
			@RequestBody final ManufacturerDto updatedManufacturerDto) {

		updatedManufacturerDto.setId(id);
		return manufacturerMapper.mapFromEntity(
				manufacturerService.updateManufacturer(manufacturerMapper.mapFromDto(updatedManufacturerDto)));
	}

	@DeleteMapping("/{id}")
	public void removeManufacturerById(@PathVariable final Long id) {
		manufacturerService.deleteManufacturerById(id);
	}
}
