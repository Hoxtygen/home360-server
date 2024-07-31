package com.codeplanks.home360.listingEnquiries;

import com.codeplanks.home360.listing.PaginatedResponse;

public interface ListingEnquiryService {
  ListingEnquiryDTO makeEnquiry(ListingEnquiry enquiryRequest);

  PaginatedResponse<ListingEnquiry> getListingEnquiriesByAgentId(int page, int size);
}
