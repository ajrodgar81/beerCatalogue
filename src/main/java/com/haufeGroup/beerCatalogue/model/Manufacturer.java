package com.haufeGroup.beerCatalogue.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Manufacturer {
	Long id;
	String name;
	String nationality;

}
