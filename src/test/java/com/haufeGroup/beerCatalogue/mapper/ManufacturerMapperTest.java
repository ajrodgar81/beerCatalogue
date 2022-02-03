package com.haufeGroup.beerCatalogue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.haufeGroup.beerCatalogue.dto.ManufacturerDto;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public class ManufacturerMapperTest {

	private static final int PAGE_INDEX = 0;

	private static final int ELEMENT_PER_PAGE = 3;

	private ManufacturerMapper testSubject;

	@BeforeEach
	public void setUp() throws Exception {
		testSubject = new ManufacturerMapper();
	}

	@Test
	public void mapFromDto() {
		Manufacturer mapFromDto = testSubject.mapFromDto(createDefaultManufacturerDto());
		assertThat(mapFromDto).as("check that the manufacturerDto is mapped").isEqualTo(createDefaultEntity());
	}

	@Test
	public void mapFromEntity() {
		ManufacturerDto mapFromDto = testSubject.mapFromEntity(createDefaultEntity());
		assertThat(mapFromDto).as("check that the entity is mapped").isEqualTo(createDefaultManufacturerDto());
	}

	@Test
	public void mergeEntity() {
		Manufacturer sourceEntity = createDefaultEntity();
		Manufacturer targetEntity = createDefaultEntityWithoutName();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity).as("check that the entity is merged").isEqualTo(sourceEntity);
	}

	@Test
	public void mergeEntityWhenSomeFieldsAreNullThenTheRelatedFieldsAreIgnored() {
		Manufacturer sourceEntity = createDefaultEntityWithoutName();
		Manufacturer targetEntity = createDefaultEntity();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity.getName()).as("check that null fields are ignored when two entities are merged")
				.isNotNull();
	}

	@Test
	public void mapFromEntityList() {
		assertThat(testSubject.mapFromEntityList(createDefaultEntityList())).as("check that entity list was mapped")
				.isEqualTo(createDefaultManufacturerDtoList());
	}

	private List<ManufacturerDto> createDefaultManufacturerDtoList() {
		return List.of(createDefaultManufacturerDto());
	}

	@Test
	public void mapFromPageEntityWhenPageIsNotEmpty() {
		Page<ManufacturerDto> beerDtoPage = testSubject.mapFromEntityPage(createDefaultPageEntity(),
				createDefaultPageable());
		assertThat(beerDtoPage.getContent()).as("check that the entity page was mapped")
				.isEqualTo(createDefaultManufacturerDtoList());
		assertThat(beerDtoPage.getPageable()).as("check that the entity page was mapped")
				.isEqualTo(createDefaultPageable());
		assertThat(beerDtoPage.getTotalElements()).as("check that the entity page was mapped")
				.isEqualTo(createDefaultPageEntity().getTotalElements());
	}

	@Test
	public void mapFromPageEntityWhenPageIsEmpty() {
		Page<ManufacturerDto> manufacturerDtoPage = testSubject.mapFromEntityPage(Page.empty(),
				createDefaultPageable());
		assertThat(manufacturerDtoPage.isEmpty()).as("check that the page content from entity was mapped").isTrue();
	}

	private Pageable createDefaultPageable() {
		return PageRequest.of(PAGE_INDEX, ELEMENT_PER_PAGE);
	}

	private Page<Manufacturer> createDefaultPageEntity() {
		return new PageImpl<Manufacturer>(createDefaultEntityList());
	}

	private List<Manufacturer> createDefaultEntityList() {
		return List.of(createDefaultEntity());
	}

	private Manufacturer createDefaultEntity() {
		Manufacturer manufacturer = createDefaultEntityWithoutName();
		manufacturer.setName("manufacturerName");
		return manufacturer;
	}

	private Manufacturer createDefaultEntityWithoutName() {
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
