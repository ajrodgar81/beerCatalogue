package com.haufeGroup.beerCatalogue.testWrappers;

import java.io.Serializable;
import java.util.List;

import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManufacturerDtoPageResponseWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int totalElements;

	private List<ManufacturerDto> content;

}
