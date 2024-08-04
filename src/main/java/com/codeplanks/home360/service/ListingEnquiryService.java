package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryDTO;

public interface ListingEnquiryService {
  ListingEnquiryDTO makeEnquiry(ListingEnquiry enquiryRequest);

  PaginatedResponse<ListingEnquiry> getListingEnquiriesByAgentId(int page, int size);
}
