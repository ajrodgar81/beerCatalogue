package com.haufeGroup.beerCatalogue.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BeerDto {

	private Long id;
	@NotEmpty(message = "beer name should be provided")
	private String name;
	@NotEmpty(message = "beer graduation should be provided")
	private String graduation;
	@NotEmpty(message = "beer type should be provided")
	private String type;
	@NotEmpty(message = "beer description should be provided")
	private String description;
	@NotNull(message = "manufacturer id should be provided")
	private Long manufacturerId;

}
