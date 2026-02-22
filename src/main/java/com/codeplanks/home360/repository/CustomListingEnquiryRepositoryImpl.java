/* (C)2024-2026 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.EnquiryStatus;
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
      Integer agentId, Integer senderId, EnquiryStatus status, Pageable pageable) {
    Query query =
        fetchListingEnquiries(agentId, senderId, status)
            .with(pageable)
            .collation(getEnglishCollation());
    List<ListingEnquiry> listingEnquiries =
        mongoTemplate.find(query, ListingEnquiry.class, "listingEnquiries");
    return PageableExecutionUtils.getPage(
        listingEnquiries,
        pageable,
        () -> mongoTemplate.count(query.limit(-1).skip(-1), ListingEnquiry.class));
  }

  private Query fetchListingEnquiries(Integer agentId, Integer senderId, EnquiryStatus status) {
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

    Criteria rootCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    if (status != null) {
      rootCriteria = rootCriteria.and("status").is(status);
    }

    return new Query(rootCriteria);
  }

  private Collation getEnglishCollation() {
    return Collation.of("en").strength(2);
  }
}
