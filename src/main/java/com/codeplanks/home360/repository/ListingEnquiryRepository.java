/* (C)2024-2025 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ListingEnquiryRepository
    extends MongoRepository<ListingEnquiry, String>, CustomListingEnquiryRepository {
  @Query(
      value = "{ 'listingId' : ?0 }",
      fields =
          "{ 'firstName' : 1, 'lastName' : 1, 'email' : 1, " + "'phoneNumber' : 1, 'listingId': 1}")
  List<ListingEnquiry> findByListingId(String listingId);
}
