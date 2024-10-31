/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listing.Listing;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CustomListingRepositoryImpl implements CustomListingRepository {
  @Autowired MongoTemplate mongoTemplate;

  @Override
  public Page<Listing> findAllWithFilter(
      String city, int annualRent, String apartmentType, Pageable pageable) {
    Query query =
        constructFilterQuery(city, annualRent, apartmentType)
            .with(pageable)
            .collation(getEnglishCollation());
    List<Listing> listings = mongoTemplate.find(query, Listing.class, "listings");
    return PageableExecutionUtils.getPage(
        listings, pageable, () -> mongoTemplate.count(query.limit(-1).skip(-1), Listing.class));
  }

  @Override
  public Page<Listing> findListingsByAgentId(Integer agentId, Pageable pageable) {
    Query query = fetchAgentListings(agentId).with(pageable).collation(getEnglishCollation());
    List<Listing> listings = mongoTemplate.find(query, Listing.class, "listings");
    return PageableExecutionUtils.getPage(
        listings, pageable, () -> mongoTemplate.count(query.limit(-1).skip(-1), Listing.class));
  }

  private Query fetchAgentListings(Integer agentId) {
    return new Query(Criteria.where("agentId").is(agentId));
  }

  private Collation getEnglishCollation() {
    return Collation.of("en").strength(2);
  }

  private Query constructFilterQuery(String city, int annualRent, String apartmentType) {
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
}
