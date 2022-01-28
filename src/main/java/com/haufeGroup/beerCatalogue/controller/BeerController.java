package com.haufeGroup.beerCatalogue.controller;

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
import com.haufeGroup.beerCatalogue.service.IBeerService;

@RestController
@RequestMapping("/api/beers")
public class BeerController {

	@Autowired
	IBeerService beerService;

	@GetMapping("/{id}")
	public Beer getBeer(@PathVariable final Long id) {
		return beerService.getBeerById(id);
	}

	@PostMapping()
	public Beer addBeer(@RequestBody final Beer beerDto) {
		return beerService.addBeer(beerDto);
	}

	@PutMapping("/{id}")
	public Beer modifyBeer(@PathVariable final Long id, @RequestBody final Beer beer) {
		beer.setId(id);
		return beerService.updateBeer(beer);
	}

	@DeleteMapping("/{id}")
	public void removeBeer(@PathVariable final Long id) {
		beerService.deleteBeer(id);
	}

}
