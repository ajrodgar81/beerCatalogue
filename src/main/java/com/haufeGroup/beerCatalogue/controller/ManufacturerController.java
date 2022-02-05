package com.haufeGroup.beerCatalogue.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/manufacturers")
@Validated
public class ManufacturerController {

	@Autowired
	private IManufacturerService manufacturerService;

	@Autowired
	ManufacturerMapper manufacturerMapper;

	@Autowired
	BeerMapper beerMapper;

	@Autowired
	SortExtractor sortExtractor;

	@Operation(summary = "Get all manufacturers with sort pagination")
	@ApiResponse(responseCode = "400", description = "Invalid sort pagination criteria supplied", content = @Content)
	@GetMapping("/")
	public Page<ManufacturerDto> getAllManufacturesWithSortPagination(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id,desc") String[] sort) {
		Pageable pagingSort = PageRequest.of(page, size, sortExtractor.extractSortCriteria(sort));
		return manufacturerMapper
				.mapFromEntityPage(manufacturerService.getAllManufacturesWithSortPagination(pagingSort), pagingSort);
	}

	@Operation(summary = "Get a manufacturer by its id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found the manufacturer", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ManufacturerDto.class)) }),
			@ApiResponse(responseCode = "204", description = "Manufacturer not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@GetMapping("/{id}")
	public ManufacturerDto getManufacturerById(
			@Parameter(description = "id of manufacturer to be searched") @Min(value = 1, message = "id value should be greater than zero") @PathVariable Long id) {
		return manufacturerMapper.mapFromEntity(manufacturerService.getManufacturerById(id));
	}

	@Operation(summary = "Get manufacturer beers by its id with sort pagination")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Manufacturer not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid sort pagination criteria supplied", content = @Content) })
	@GetMapping("/{id}/beers")
	public Page<BeerDto> getManufacturerBeersWithSortPagination(
			@Parameter(description = "id of manufacturer to be searched") @Min(value = 1, message = "manufacturer id value should be greater than zero") @PathVariable(name = "id") Long manufacturerId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "id,desc") String[] sort) {
		Pageable pagingSort = PageRequest.of(page, size, sortExtractor.extractSortCriteria(sort));
		return beerMapper.mapFromEntityPage(
				manufacturerService.getManufacturerBeersWithSortPagination(manufacturerId, pagingSort), pagingSort);
	}

	@Operation(summary = "Add new manufacturer")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "the manufacturer was added", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ManufacturerDto.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid manufacturer supplied", content = @Content) })
	@PostMapping("/")
	public ManufacturerDto addManufacturer(@Valid @RequestBody final ManufacturerDto newManufacturerDto) {
		return manufacturerMapper
				.mapFromEntity(manufacturerService.addNewManufacturer(manufacturerMapper.mapFromDto(newManufacturerDto)));
	}

	@Operation(summary = "Modify a manufacturer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "the manufacturer was modified", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ManufacturerDto.class)) }),
			@ApiResponse(responseCode = "404", description = "The manufacturer was not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid manufacturer supplied", content = @Content) })
	@PutMapping("/{id}")
	public ManufacturerDto modifyManufacturer(
			@Parameter(description = "id of manufacturer to be modified") @Min(value = 1, message = "manufacturer id value should be greater than zero") @PathVariable Long id,
			@Valid @RequestBody final ManufacturerDto updatedManufacturerDto) {
		updatedManufacturerDto.setId(id);
		return manufacturerMapper.mapFromEntity(
				manufacturerService.updateManufacturer(manufacturerMapper.mapFromDto(updatedManufacturerDto)));
	}

	@Operation(summary = "Delete a manufacturer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "the manufacturer was removed", content = @Content),
			@ApiResponse(responseCode = "404", description = "The manufacturer was not found", content = @Content) })
	@DeleteMapping("/{id}")
	public void removeManufacturerById(
			@Parameter(description = "id of manufacturer to be removed") @Min(value = 1, message = "manufacturer id value should be greater than zero") @PathVariable final Long id) {
		manufacturerService.deleteManufacturerById(id);
	}
}
