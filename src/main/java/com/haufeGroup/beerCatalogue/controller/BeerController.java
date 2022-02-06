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
import com.haufeGroup.beerCatalogue.mapper.BeerMapper;
import com.haufeGroup.beerCatalogue.service.IBeerService;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/beers")
@Validated
public class BeerController {

	@Autowired
	IBeerService beerService;

	@Autowired
	BeerMapper modelMapper;

	@Autowired
	SortExtractor sortExtractor;

	@Operation(summary = "Get all beers with sort pagination")
	@ApiResponse(responseCode = "400", description = "Invalid sort pagination criteria supplied", content = @Content)
	@GetMapping("/")
	public Page<BeerDto> getAllBeersWithSortPagination(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id,desc") String[] sort) {
		Pageable pagingSort = PageRequest.of(page, size, sortExtractor.extractSortCriteria(sort));
		return modelMapper.mapFromEntityPage(beerService.getAllBeersWithSortPagination(pagingSort), pagingSort);
	}

	@Operation(summary = "Get a beer by its id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the beer", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = BeerDto.class)) }),
			@ApiResponse(responseCode = "204", description = "Beer not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@GetMapping("/{id}")
	public BeerDto getBeerById(
			@Parameter(description = "id of beer to be searched") @Min(value = 1, message = "beer id value should be greater than zero") @PathVariable final Long id) {
		return modelMapper.mapFromEntity(beerService.getBeerById(id));
	}

	@Operation(summary = "Add new beer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The beer was added", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = BeerDto.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid beer supplied", content = @Content) })
	@PostMapping("/")
	public BeerDto addBeer(@Valid @RequestBody final BeerDto beerDto) {

		return modelMapper.mapFromEntity(beerService.addNewBeer(modelMapper.mapFromDto(beerDto)));
	}

	@Operation(summary = "Modify a beer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The beer was modified", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = BeerDto.class)) }),
			@ApiResponse(responseCode = "404", description = "Beer not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid beer supplied", content = @Content) })
	@PutMapping("/{id}")
	public BeerDto modifyBeer(
			@Parameter(description = "id of beer to be modified") @Min(value = 1, message = "beer id value should be greater than zero") @PathVariable final Long id,
			@Valid @RequestBody final BeerDto beerDto) {
		beerDto.setId(id);
		return modelMapper.mapFromEntity(beerService.updateBeer(modelMapper.mapFromDto(beerDto)));
	}

	@Operation(summary = "Delete a beer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The beer was removed", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = BeerDto.class)) }),
			@ApiResponse(responseCode = "404", description = "Beer not found", content = @Content) })
	@DeleteMapping("/{id}")
	public void removeBeerById(
			@Parameter(description = "id of beer to be removed") @Min(value = 1, message = "beer id value should be greater than zero") @PathVariable final Long id) {
		beerService.deleteBeerById(id);
	}

}
