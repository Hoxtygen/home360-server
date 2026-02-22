/* (C)2024-2025 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingEnquiryRepository
    extends MongoRepository<ListingEnquiry, String>, CustomListingEnquiryRepository {
  @Query(value = "{ 'agentId': ?0 }", fields = "{ 'unreadCountByAgent': 1 }")
  List<ListingEnquiry> findAgentUnreadCounts(int agentId);

  @Query(value = "{ 'userId': ?0 }", fields = "{ 'unreadCountByInquirer': 1 }")
  List<ListingEnquiry> findInquirerUnreadCounts(int userId);

  @Query("{'listingId': ?0}")
  List<ListingEnquiry> findByListingId(String listingId);
}