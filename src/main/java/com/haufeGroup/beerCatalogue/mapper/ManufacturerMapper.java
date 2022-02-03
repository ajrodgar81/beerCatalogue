package com.haufeGroup.beerCatalogue.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

@Component
public class ManufacturerMapper {

	private ModelMapper modelMapper;

	public Manufacturer mapFromDto(final ManufacturerDto ManufacturerDto) {
		return getModelMapper().map(ManufacturerDto, Manufacturer.class);
	}

	public ManufacturerDto mapFromEntity(final Manufacturer entity) {
		return getModelMapper().map(entity, ManufacturerDto.class);
	}

	public void mergeEntity(final Manufacturer sourceEntity, final Manufacturer targetEntity) {
		getModelMapper().map(sourceEntity, targetEntity);
	}

	public List<ManufacturerDto> mapFromEntityList(final List<Manufacturer> entityList) {
		return entityList.stream().map(entity -> mapFromEntity(entity)).collect(Collectors.toList());
	}

	public Page<ManufacturerDto> mapFromEntityPage(final Page<Manufacturer> entityPage, final Pageable sortPageable) {
		return new PageImpl<ManufacturerDto>(mapFromEntityList(entityPage.getContent()), sortPageable,
				entityPage.getTotalElements());
	}

	private ModelMapper getModelMapper() {
		if (modelMapper == null) {
			modelMapper = new ModelMapper();
			this.modelMapper.getConfiguration().setSkipNullEnabled(true);
		}
		return modelMapper;
	}

}
