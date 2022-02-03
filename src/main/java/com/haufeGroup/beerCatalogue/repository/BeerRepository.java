package com.haufeGroup.beerCatalogue.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haufeGroup.beerCatalogue.model.Beer;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {
	
	public List<Beer> findByManufacturerId(final Long manufacturerId, final Sort sortCriteria);
	
	public Page<Beer> findByManufacturerId(final Long manufacturerId, final Pageable pageable);

}
