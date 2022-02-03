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
import com.haufeGroup.beerCatalogue.mapper.BeerMapper;
import com.haufeGroup.beerCatalogue.service.IBeerService;
import com.haufeGroup.beerCatalogue.util.SortExtractor;

@RestController
@RequestMapping("/api/beers")
public class BeerController {

	@Autowired
	IBeerService beerService;

	@Autowired
	BeerMapper modelMapper;

	@Autowired
	SortExtractor sortExtractor;

	@GetMapping("/")
	public Page<BeerDto> getAllBeersWithSortPagination(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id,desc") String[] sort) {
		Pageable pagingSort = PageRequest.of(page, size, sortExtractor.extractSortCriteria(sort));
		return modelMapper.mapFromEntityPage(beerService.getAllBeersWithSortPagination(pagingSort), pagingSort);
	}

	@GetMapping("/{id}")
	public BeerDto getBeer(@PathVariable final Long id) {
		return modelMapper.mapFromEntity(beerService.getBeerById(id));
	}

	@PostMapping("/")
	public BeerDto addBeer(@RequestBody final BeerDto beerDto) {
		return modelMapper.mapFromEntity(beerService.addBeer(modelMapper.mapFromDto(beerDto)));
	}

	@PutMapping("/{id}")
	public BeerDto modifyBeer(@PathVariable final Long id, @RequestBody final BeerDto beerDto) {
		beerDto.setId(id);
		return modelMapper.mapFromEntity(beerService.updateBeer(modelMapper.mapFromDto(beerDto)));
	}

	@DeleteMapping("/{id}")
	public void removeBeerById(@PathVariable final Long id) {
		beerService.deleteBeerById(id);
	}

}
