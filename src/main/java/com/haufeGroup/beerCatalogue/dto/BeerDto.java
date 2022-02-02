package com.haufeGroup.beerCatalogue.dto;

import lombok.Data;

@Data
public class BeerDto {

	private Long id;
	private String name;
	private String graduation;
	private String type;
	private String description;
	private Long manufacturerId;

}
