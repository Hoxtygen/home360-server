/* (C)2024-2026 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listingEnquiries.*;
import java.util.List;

public interface ListingEnquiryService {
  ListingEnquiry makeEnquiry(ListingEnquiryDTO enquiryRequest);

  PaginatedListingEnquiriesResponse getListingEnquiries(
      int page, int size, Integer senderId, int agentId);

  ListingEnquiry getListingEnquiryById(String enquiryMessageId);

  Boolean markEnquiryAsRead(String enquiryMessageId);

  List<ListingEnquiry> getEnquiriesByListingId(String listingId);
}
