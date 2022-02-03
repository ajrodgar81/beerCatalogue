package com.haufeGroup.beerCatalogue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public Page<ManufacturerDto> getAllManufacturesWithSortPagination(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id,desc") String[] sort) {
		Pageable pagingSort = PageRequest.of(page, size, sortExtractor.extractSortCriteria(sort));
		return manufacturerMapper
				.mapFromEntityPage(manufacturerService.getAllManufacturesWithSortPagination(pagingSort), pagingSort);
	}

	@GetMapping("/{id}")
	public ManufacturerDto getManufacturerById(@PathVariable Long id) {
		return manufacturerMapper.mapFromEntity(manufacturerService.getManufacturerById(id));
	}

	@GetMapping("/{id}/beers")
	public Page<BeerDto> getManufacturerBeersWithSortPagination(@PathVariable(name = "id") Long manufacturerId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "id,desc") String[] sort) {
		Pageable pagingSort = PageRequest.of(page, size, sortExtractor.extractSortCriteria(sort));
		return beerMapper.mapFromEntityPage(
				manufacturerService.getManufacturerBeersWithSortPagination(manufacturerId, pagingSort), pagingSort);
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
