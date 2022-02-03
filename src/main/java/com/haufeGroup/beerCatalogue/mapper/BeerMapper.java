package com.haufeGroup.beerCatalogue.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.haufeGroup.beerCatalogue.dto.BeerDto;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

@Component
public class BeerMapper {

	private ModelMapper modelMapper;

	public Beer mapFromDto(final BeerDto beerDto) {
		return getModelMapper().map(beerDto, Beer.class);
	}

	public BeerDto mapFromEntity(final Beer entity) {
		return getModelMapper().map(entity, BeerDto.class);
	}

	public void mergeEntity(final Beer sourceEntity, final Beer targetEntity) {
		getModelMapper().map(sourceEntity, targetEntity);
	}

	public List<BeerDto> mapFromEntityList(final List<Beer> entityList) {
		return entityList.stream().map(entity -> mapFromEntity(entity)).collect(Collectors.toList());
	}

	public Page<BeerDto> mapFromEntityPage(final Page<Beer> entityPage, final Pageable sortPageable) {
		return new PageImpl<BeerDto>(mapFromEntityList(entityPage.getContent()), sortPageable,
				entityPage.getTotalElements());
	}

	private ModelMapper getModelMapper() {
		if (modelMapper == null) {
			modelMapper = new ModelMapper();
			this.modelMapper.getConfiguration().setSkipNullEnabled(true);
			this.modelMapper.getConfiguration().setImplicitMappingEnabled(true);
			addConverterFromDtoToEntity();
			addConverterFromEntityToDto();
		}
		return modelMapper;
	}

	private void addConverterFromEntityToDto() {
		TypeMap<Beer, BeerDto> propertyMapper = this.modelMapper.createTypeMap(Beer.class, BeerDto.class);
		Converter<Beer, BeerDto> beerToBeerDto = c -> {
			BeerDto beerDto = new BeerDto();
			beerDto.setId(c.getSource().getId());
			beerDto.setName(c.getSource().getName());
			beerDto.setDescription(c.getSource().getDescription());
			beerDto.setGraduation(c.getSource().getGraduation());
			beerDto.setType(c.getSource().getType());
			beerDto.setManufacturerId(c.getSource().getManufacturer().getId());
			return beerDto;
		};
		propertyMapper.setConverter(beerToBeerDto);
	}

	private void addConverterFromDtoToEntity() {
		TypeMap<BeerDto, Beer> propertyMapper = this.modelMapper.createTypeMap(BeerDto.class, Beer.class);
		Converter<BeerDto, Beer> beerDtoToBeer = src -> {
			Beer entity = new Beer();
			entity.setId(src.getSource().getId());
			entity.setName(src.getSource().getName());
			entity.setDescription(src.getSource().getDescription());
			entity.setGraduation(src.getSource().getGraduation());
			entity.setType(src.getSource().getType());
			mapManufacturerFromDto(src.getSource(), entity);
			return entity;
		};
		propertyMapper.setConverter(beerDtoToBeer);
	}

	private void mapManufacturerFromDto(final BeerDto source, final Beer entity) {
		if (source.getManufacturerId() != null) {
			Manufacturer manufacturer = new Manufacturer();
			manufacturer.setId(source.getManufacturerId());
			entity.setManufacturer(manufacturer);
		}
	}

}
