package com.haufeGroup.beerCatalogue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.haufeGroup.beerCatalogue.dto.BeerDto;
import com.haufeGroup.beerCatalogue.model.Beer;
import com.haufeGroup.beerCatalogue.model.Manufacturer;

public class BeerMapperTest {

	private BeerMapper testSubject;

	@BeforeEach
	public void setUp() {
		testSubject = new BeerMapper();
	}

	@Test
	public void mapFromDtoWhenManufacturerIdIsProvided() {
		assertThat(testSubject.mapFromDto(createDefaultBeerDto())).as("check that the beerDto is mapped")
				.isEqualTo(createDefaultBeer());
	}

	@Test
	public void mapFromDtoWhenManufacturerIdIsNotProvided() {
		assertThat(testSubject.mapFromDto(createDefaultBeerDtoWithoutManufacturerId()))
				.as("check that the beerDto is mapped").isEqualTo(createDefaultBeerWithouManufacturer());
	}

	@Test
	public void mapFromEntity() {
		BeerDto mapFromDto = testSubject.mapFromEntity(createDefaultBeer());
		assertThat(mapFromDto).as("check that the beer is mapped").isEqualTo(createDefaultBeerDto());
	}

	@Test
	public void mergeEntity() {
		Beer sourceEntity = createDefaultBeer();
		Beer targetEntity = new Beer();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity).as("check that the entity is merged").isEqualTo(sourceEntity);
	}

	@Test
	public void mergeEntityWhenSomeFieldsAreNullThenTheRelatedFieldsAreIgnored() {
		Beer sourceEntity = createDefaultBeerWithoutDescription();
		Beer targetEntity = createDefaultBeer();
		testSubject.mergeEntity(sourceEntity, targetEntity);
		assertThat(targetEntity.getDescription()).as("check that null fields are ignored when two entities are merged")
				.isNotNull();
	}

	private Beer createDefaultBeerWithouManufacturer() {
		Beer beer = createDefaultBeer();
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

	private Beer createDefaultBeer() {
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
