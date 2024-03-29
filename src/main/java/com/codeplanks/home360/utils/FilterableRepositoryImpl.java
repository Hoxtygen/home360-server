package com.codeplanks.home360.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;


/**
 * @author Wasiu Idowu
 *
 * */

public class FilterableRepositoryImpl<T> implements FilterableRepository<T> {
  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Page<T> findAllWithFilter(Class<T> typeParameterClass, String city, int annualRent,
                                   String apartmentType, Pageable pageable) {
    Collation collation = Collation.of("en").strength(2);
    Query query =
            constructFilterQuery(city, annualRent, apartmentType).with(pageable)
                    .collation(collation);
    List<T> listings = mongoTemplate.find(query, typeParameterClass, "listings");
    return PageableExecutionUtils.getPage(listings, pageable,
            () -> mongoTemplate.count(query.limit(-1).skip(-1), typeParameterClass));
  }

  @Override
  public Page<T> findListingsByAgentId(Class<T> typeParameter, Integer agentId, Pageable pageable) {
    Collation collation = Collation.of("en").strength(2);
    Query query   = fetchAgentListings(agentId).with(pageable).collation(collation);
    List<T> listings  = mongoTemplate.find(query, typeParameter, "listings");
    return PageableExecutionUtils.getPage(listings, pageable,
            ()-> mongoTemplate.count(query.limit(-1).skip(-1), typeParameter));
  }
}
