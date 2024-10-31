/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import java.util.ArrayList;
import java.util.List;
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
public class CustomListingEnquiryRepositoryImpl implements CustomListingEnquiryRepository {
  @Autowired private MongoTemplate mongoTemplate;

  @Override
  public Page<ListingEnquiry> findListingEnquiries(
      Integer agentId, Integer senderId, Pageable pageable) {
    Query query =
        fetchListingEnquiries(agentId, senderId).with(pageable).collation(getEnglishCollation());
    List<ListingEnquiry> listingEnquiries =
        mongoTemplate.find(query, ListingEnquiry.class, "listingEnquiries");
    return PageableExecutionUtils.getPage(
        listingEnquiries,
        pageable,
        () -> mongoTemplate.count(query.limit(-1).skip(-1), ListingEnquiry.class));
  }

  private Query fetchListingEnquiries(Integer agentId, Integer senderId) {
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

  private Collation getEnglishCollation() {
    return Collation.of("en").strength(2);
  }
}
