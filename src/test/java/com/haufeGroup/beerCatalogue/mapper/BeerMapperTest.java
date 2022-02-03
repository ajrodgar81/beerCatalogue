package com.haufeGroup.beerCatalogue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.haufeGroup.beerCatalogue.dto.BeerDto;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public class BeerMapperTest {

	private static final int PAGE_INDEX = 0;

	private static final int ELEMENT_PER_PAGE = 3;

	private BeerMapper testSubject;

	@BeforeEach
	public void setUp() {
		testSubject = new BeerMapper();
	}

	@Test
	public void mapFromDtoWhenManufacturerIdIsProvided() {
		assertThat(testSubject.mapFromDto(createDefaultBeerDto())).as("check that the beerDto is mapped")
				.isEqualTo(createDefaultEntity());
	}

	@Test
	public void mapFromDtoWhenManufacturerIdIsNotProvided() {
		assertThat(testSubject.mapFromDto(createDefaultBeerDtoWithoutManufacturerId()))
				.as("check that the beerDto is mapped").isEqualTo(createDefaultEntityWithouManufacturer());
	}

	@Test
	public void mapFromEntity() {
		BeerDto mapFromDto = testSubject.mapFromEntity(createDefaultEntity());
		assertThat(mapFromDto).as("check that the entity is mapped").isEqualTo(createDefaultBeerDto());
	}

	@Test
	public void mergeEntity() {
		Beer sourceEntity = createDefaultEntity();
		Beer targetEntity = new Beer();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity).as("check that the entity is merged").isEqualTo(sourceEntity);
	}

	@Test
	public void mergeEntityWhenSomeFieldsAreNullThenTheRelatedFieldsAreIgnored() {
		Beer sourceEntity = createDefaultBeerWithoutDescription();
		Beer targetEntity = createDefaultEntity();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity.getDescription()).as("check that null fields are ignored when two entities are merged")
				.isNotNull();
	}

	@Test
	public void mapFromEntityList() {
		assertThat(testSubject.mapFromEntityList(createDefaultEntityList())).as("check that entity list was mapped")
				.isEqualTo(createDefaultBeerDtoList());
	}

	@Test
	public void mapFromPageEntityWhenPageIsNotEmpty() {
		Page<BeerDto> beerDtoPage = testSubject.mapFromEntityPage(createDefaultPageEntity(), createDefaultPageable());
		assertThat(beerDtoPage.getContent()).as("check that the entity page was mapped")
				.isEqualTo(createDefaultBeerDtoList());
		assertThat(beerDtoPage.getPageable()).as("check that the entity page was mapped")
				.isEqualTo(createDefaultPageable());
		assertThat(beerDtoPage.getTotalElements()).as("check that the entity page was mapped")
				.isEqualTo(createDefaultPageEntity().getTotalElements());
	}

	@Test
	public void mapFromPageEntityWhenPageIsEmpty() {
		Page<BeerDto> beerDtoPage = testSubject.mapFromEntityPage(Page.empty(), createDefaultPageable());
		assertThat(beerDtoPage.isEmpty()).as("check that the page content from entity was mapped").isTrue();
	}

	private List<BeerDto> createDefaultBeerDtoList() {
		return List.of(createDefaultBeerDto());
	}

	private Pageable createDefaultPageable() {
		return PageRequest.of(PAGE_INDEX, ELEMENT_PER_PAGE);
	}

	private Page<Beer> createDefaultPageEntity() {
		return new PageImpl<Beer>(createDefaultEntityList());
	}

	private Beer createDefaultEntityWithouManufacturer() {
		Beer beer = createDefaultEntity();
		beer.setManufacturer(null);
		return beer;
	}

	private BeerDto createDefaultBeerDtoWithoutManufacturerId() {
		BeerDto beerDto = new BeerDto();
		beerDto.setId(1L);
		beerDto.setName("beerName");
		beerDto.setGraduation("graduation");
		beerDto.setDescription("description");
		beerDto.setType("beerType");
		return beerDto;
	}

	private BeerDto createDefaultBeerDto() {
		BeerDto beerDto = createDefaultBeerDtoWithoutManufacturerId();
		beerDto.setManufacturerId(1L);
		return beerDto;
	}

	private List<Beer> createDefaultEntityList() {
		return List.of(createDefaultEntity());
	}

	private Beer createDefaultEntity() {
		Beer beer = createDefaultBeerWithoutDescription();
		beer.setDescription("description");
		return beer;
	}

	private Beer createDefaultBeerWithoutDescription() {
		Beer beer = new Beer();
		beer.setId(1L);
		beer.setGraduation("graduation");
		beer.setName("beerName");
		beer.setType("beerType");
		beer.setManufacturer(createDefaultManufacturer());
		return beer;
	}

	private Manufacturer createDefaultManufacturer() {
		Manufacturer manufacurer = new Manufacturer();
		manufacurer.setId(1L);
		return manufacurer;
	}

}
