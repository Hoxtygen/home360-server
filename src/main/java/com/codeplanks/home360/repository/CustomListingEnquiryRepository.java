/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.EnquiryStatus;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomListingEnquiryRepository {
  Page<ListingEnquiry> findListingEnquiries(
      Integer agentId, Integer senderId, EnquiryStatus status, Pageable pageable);
}
