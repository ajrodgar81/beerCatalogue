package com.haufeGroup.beerCatalogue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public class ManufacturerMapperTest {

	private ManufacturerMapper testSubject;

	@BeforeEach
	public void setUp() throws Exception {
		testSubject = new ManufacturerMapper();
	}

	@Test
	public void mapFromDto() {
		Manufacturer mapFromDto = testSubject.mapFromDto(createDefaultManufacturerDto());
		assertThat(mapFromDto).as("check that the manufacturerDto is mapped").isEqualTo(createDefaultManufacturer());
	}

	@Test
	public void mapFromEntity() {
		ManufacturerDto mapFromDto = testSubject.mapFromEntity(createDefaultManufacturer());
		assertThat(mapFromDto).as("check that the manufacturer is mapped").isEqualTo(createDefaultManufacturerDto());
	}

	@Test
	public void mergeEntity() {
		Manufacturer sourceEntity = createDefaultManufacturer();
		Manufacturer targetEntity = createDefaultManufacturerWithoutName();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity).as("check that the entity is merged").isEqualTo(sourceEntity);
	}

	@Test
	public void mergeEntityWhenSomeFieldsAreNullThenTheRelatedFieldsAreIgnored() {
		Manufacturer sourceEntity = createDefaultManufacturerWithoutName();
		Manufacturer targetEntity = createDefaultManufacturer();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity.getName()).as("check that null fields are ignored when two entities are merged")
				.isNotNull();
	}

	private Manufacturer createDefaultManufacturer() {
		Manufacturer manufacturer = createDefaultManufacturerWithoutName();
		manufacturer.setName("manufacturerName");
		return manufacturer;
	}

	private Manufacturer createDefaultManufacturerWithoutName() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setId(1L);
		manufacturer.setNationality("nationality");
		return manufacturer;
	}

	private ManufacturerDto createDefaultManufacturerDto() {
		ManufacturerDto manufacturerDto = new ManufacturerDto();
		manufacturerDto.setId(1L);
		manufacturerDto.setName("manufacturerName");
		manufacturerDto.setNationality("nationality");
		return manufacturerDto;
	}
}
