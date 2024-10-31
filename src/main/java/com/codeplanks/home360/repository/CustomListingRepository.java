/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listing.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomListingRepository {
  Page<Listing> findAllWithFilter(
      String city, int annualRent, String apartmentType, Pageable pageable);

  Page<Listing> findListingsByAgentId(Integer agentId, Pageable pageable);
}
