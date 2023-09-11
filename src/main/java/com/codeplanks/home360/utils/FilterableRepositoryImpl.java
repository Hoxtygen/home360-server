package com.codeplanks.home360.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class FilterableRepositoryImpl<T> implements FilterableRepository<T> {
  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Page<T> findAllWithFilter(Class<T> typeParameterClass, Filtering filtering,
                                   Pageable pageable) {
    Collation collation = Collation.of("en").strength(2);
    Query query = constructQueryFromFiltering(filtering).with(pageable).collation(collation);
    List<T> ts = mongoTemplate.find(query, typeParameterClass);
    return PageableExecutionUtils.getPage(ts, pageable,
            () -> mongoTemplate.count(query.limit(-1).skip(-1),
                    typeParameterClass));
  }

  @Override
  public List<Object> getAllPossibleValuesForFilter(
          Class<T> typeParameterClass,
          Filtering filtering, String filterKey
  ) {
    Query query = constructQueryFromFiltering(filtering);
    return mongoTemplate.query(typeParameterClass).distinct(filterKey).matching(query).all();
  }

}
