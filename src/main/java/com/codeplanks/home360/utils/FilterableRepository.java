package com.codeplanks.home360.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.Map;

public interface FilterableRepository<T> {
  Page<T> findAllWithFilter(Class<T> typeParameterClass, String city, int annualRent,
                            String apartmentType,
                            Pageable pageable);

  default Query constructFilterQuery(String city, int annualRent, String apartmentType) {
    Query query = new Query();
    Map<String, Criteria> criteriaMap = new HashMap<>();
    Criteria cityCriteria = Criteria.where("address.city").is(city);
    Criteria annualRentCriteria = Criteria.where("cost.annualRent").gte(annualRent);
    Criteria apartmentTypeCriteria =
            Criteria.where("apartmentInfo.apartmentType").is(apartmentType);
    Criteria apartmentAvailableCriteria = Criteria.where("available").is(true);

    if (city != null && !city.isEmpty()) {
      criteriaMap.put("address.city", cityCriteria);
    }

    if (apartmentType!= null && !apartmentType.isEmpty() ) {
      criteriaMap.put("apartmentInfo.apartmentType", apartmentTypeCriteria);
    }
    criteriaMap.put("cost.annualRent", annualRentCriteria);
    criteriaMap.put("available", apartmentAvailableCriteria);

    criteriaMap.values().forEach(query::addCriteria);

    return  query;
  }

}
