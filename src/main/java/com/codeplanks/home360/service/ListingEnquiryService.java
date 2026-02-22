/* (C)2024-2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listingEnquiries.*;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ListingEnquiryService {
  ListingEnquiry makeEnquiry(ListingEnquiryDTO enquiryRequest);

  PaginatedListingEnquiriesResponse getListingEnquiries(
      int page, int size, Integer senderId, int agentId);

  ListingEnquiry getListingEnquiryById(String enquiryMessageId);

  Boolean markMessageAsRead(String enquiryMessageId);

  List<ListingEnquiry> getEnquiriesByListingId(String listingId);
}
