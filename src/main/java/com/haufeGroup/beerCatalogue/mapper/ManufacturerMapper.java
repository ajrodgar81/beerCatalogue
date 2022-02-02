package com.haufeGroup.beerCatalogue.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

@Component
public class ManufacturerMapper {

	private ModelMapper modelMapper;

	public Manufacturer mapFromDto(ManufacturerDto ManufacturerDto) {
		return getModelMapper().map(ManufacturerDto, Manufacturer.class);
	}

	public ManufacturerDto mapFromEntity(Manufacturer Manufacturer) {
		return getModelMapper().map(Manufacturer, ManufacturerDto.class);
	}

	public void mergeEntity(Manufacturer sourceEntity, Manufacturer targetEntity) {
		getModelMapper().map(sourceEntity, targetEntity);
	}

	public List<ManufacturerDto> mapFromEntityList(List<Manufacturer> manufacturerList) {
		return manufacturerList.stream().map(manufacturer -> mapFromEntity(manufacturer)).collect(Collectors.toList());
	}

	private ModelMapper getModelMapper() {

		if (modelMapper == null) {
			modelMapper = new ModelMapper();
			this.modelMapper.getConfiguration().setSkipNullEnabled(true);
		}
		return modelMapper;
	}

}
