package com.haufeGroup.beerCatalogue.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.haufeGroup.beerCatalogue.exception.SortExtractorException;

@Component
public class SortExtractor {

	public static final String INVALID_FORMAT = "The format of the sort criteria provided is not valid. The format is: {sort=\"field1, direction1\", sort=\"field2, direction2\"...} or {sort=field, sort=direction}.";

	public Sort extractSortCriteria(String[] sort) {
		try {
			List<Order> orders = new ArrayList<Order>();
			if (sort[0].contains(",")) {
				// will sort more than 2 fields
				// sortOrder="field, direction"
				for (String sortOrder : sort) {
					String[] _sort = StringUtils.trimAllWhitespace(sortOrder).split(",");
					orders.add(new Order(Direction.fromString(_sort[1]), _sort[0]));
				}
			} else {
				// sort=[field, direction]
				checkExpectedSortSizeForASingleField(sort);
				orders.add(new Order(Direction.fromString(sort[1]), sort[0]));
			}
			return Sort.by(orders);
		} catch (Exception ex) {
			throw new SortExtractorException(INVALID_FORMAT);
		}
	}

	private void checkExpectedSortSizeForASingleField(String[] sort) {
		if (sort.length != 2) {
			throw new SortExtractorException(INVALID_FORMAT);
		}
	}

}
