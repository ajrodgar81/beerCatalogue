package com.haufeGroup.beerCatalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.haufeGroup.beerCatalogue.model.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

}
