package com.haufeGroup.beerCatalogue.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ManufacturerDto {

	private Long id;
	@NotEmpty(message = "manufacturer name should be provided")
	private String name;
	@NotEmpty(message = "manufacturer nationality should be provided")
	private String nationality;

}
