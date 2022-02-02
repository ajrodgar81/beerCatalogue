package com.haufeGroup.beerCatalogue.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.haufeGroup.beerCatalogue.exception.SortExtractorException;

public class SortExtractorTest {

	private static SortExtractor testSubject;

	@BeforeAll
	public static void setUp() {
		testSubject = new SortExtractor();
	}

	@Test
	public void extractValidMultipleSortCriteria() {
		assertThat(testSubject.extractSortCriteria(new String[] { "field1, asc", "field2, desc" }))
				.as("check that the sort criteria is extracted").isNotNull();
	}

	@Test
	public void extractMultipleSortCriteriaWhenAtLeastOneCriteriaContainsAnInvalidDirection() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "field1, unknown", "field2, desc" }));

	}

	@Test
	public void extractMultipleSortCriteriaWhenAtLeastOneCriteriaNotContainsADirection() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "field2, desc", "field1, " }));

	}

	@Test
	public void extractMultipleSortCriteriaWhenAtLeastOneCriteriaNotContainsAField() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "field1, desc", ", asc" }));
	}

	@Test
	public void extractMultipleSortCriteriaWhenAtLeastOneCriteriaHasAWrongFormat() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "field2, desc", ", , asc" }));

	}

	@Test
	public void extractValidSingleSortCriteria() {
		assertThat(testSubject.extractSortCriteria(new String[] { "field", "desc" }))
				.as("check that the sort criteria is extracted").isNotNull();
	}

	@Test
	public void extractSortCriteriaInWrongFormat() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "field", "asc", "desc" }));

	}

	@Test
	public void extractSingleSortCriteriaWhenTheFieldIsNotProvided() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "", "asc" }));
	}

	@Test
	public void extractSingleSortCriteriaWhenTheDirectionIsNotProvided() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "field", "" }));
	}

	@Test
	public void extractSingleSortCriteriaWhenTheDirectionIsNotValid() {
		Assert.assertThrows(SortExtractorException.class,
				() -> testSubject.extractSortCriteria(new String[] { "field", "unkown" }));
	}
}
