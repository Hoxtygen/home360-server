/* (C)2024 */
package com.codeplanks.home360.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author Wasiu Idowu
 */
public interface FilterableRepository<T> {
  Page<T> findAllWithFilter(
      Class<T> typeParameterClass,
      String city,
      int annualRent,
      String apartmentType,
      Pageable pageable);

  Page<T> findListingsByAgentId(Class<T> typeParameterClass, Integer agentId, Pageable pageable);

  Page<T> findListingEnquiries(
      Class<T> typeParameterClass, Integer agentId, Integer senderId, Pageable pageable);

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

    if (apartmentType != null && !apartmentType.isEmpty()) {
      criteriaMap.put("apartmentInfo.apartmentType", apartmentTypeCriteria);
    }
    criteriaMap.put("cost.annualRent", annualRentCriteria);
    criteriaMap.put("available", apartmentAvailableCriteria);

    criteriaMap.values().forEach(query::addCriteria);

    return query;
  }

  default Query fetchAgentListings(Integer agentId) {
    Query query = new Query();
    Map<String, Criteria> criteriaMap = new HashMap<>();
    Criteria agentCriteria = Criteria.where("agentId").is(agentId);
    criteriaMap.put("agentId", agentCriteria);
    criteriaMap.values().forEach((query::addCriteria));
    return query;
  }

  default Query fetchListingEnquiries(Integer agentId, Integer senderId) {
    List<Criteria> criteriaList = new ArrayList<>();
    if (agentId != null) {
      criteriaList.add(Criteria.where("agentId").is(agentId));
    }
    if (senderId != null) {
      criteriaList.add(Criteria.where("userId").is(senderId));
    }

    if (criteriaList.isEmpty()) {
      throw new IllegalArgumentException("At least one of agentId or senderId must be provided");
    }

    return new Query(new Criteria().orOperator(criteriaList.toArray(new Criteria[0])));
  }
}
