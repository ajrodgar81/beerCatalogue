package com.haufeGroup.beerCatalogue.testWrappers;

import java.io.Serializable;
import java.util.List;

import com.haufeGroup.beerCatalogue.dto.BeerDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeerDtoPageResponseWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int totalElements;

	private List<BeerDto> content;

}
