/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingView.ListingView;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ListingViewRepository extends MongoRepository<ListingView, String> {
  Long countByListingId(String listingId);
}
