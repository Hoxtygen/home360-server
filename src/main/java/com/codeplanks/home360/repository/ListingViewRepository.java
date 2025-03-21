/* (C)2024-2025 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingView.ListingView;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingViewRepository extends MongoRepository<ListingView, String> {
  Long countByListingId(String listingId);

  List<ListingView> findByListingId(String listingId);
}
