package com.haufeGroup.beerCatalogue.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.haufeGroup.beerCatalogue.dto.BeerDto;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

@Component
public class BeerMapper {

	private ModelMapper modelMapper;

	public Beer mapFromDto(BeerDto beerDto) {
		return getModelMapper().map(beerDto, Beer.class);
	}

	public BeerDto mapFromEntity(Beer beer) {
		return getModelMapper().map(beer, BeerDto.class);
	}

	public void mergeEntity(Beer sourceEntity, Beer targetEntity) {
		getModelMapper().map(sourceEntity, targetEntity);
	}

	public List<BeerDto> mapFromEntityList(List<Beer> beerList) {
		return beerList.stream().map(beer -> mapFromEntity(beer)).collect(Collectors.toList());
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
			Beer beer = new Beer();
			beer.setId(src.getSource().getId());
			beer.setName(src.getSource().getName());
			beer.setDescription(src.getSource().getDescription());
			beer.setGraduation(src.getSource().getGraduation());
			beer.setType(src.getSource().getType());
			mapManufacturerFromDto(src.getSource(), beer);
			return beer;
		};
		propertyMapper.setConverter(beerDtoToBeer);
	}

	private void mapManufacturerFromDto(BeerDto source, Beer beer) {
		if (source.getManufacturerId() != null) {
			Manufacturer manufacturer = new Manufacturer();
			manufacturer.setId(source.getManufacturerId());
			beer.setManufacturer(manufacturer);
		}
	}

}
