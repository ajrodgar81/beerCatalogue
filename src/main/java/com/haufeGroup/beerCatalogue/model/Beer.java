package com.haufeGroup.beerCatalogue.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Beer {
	Long id;
	String beerName;
	String graduation;
	String type;
	String description;
	Long manufacturerId;
}
