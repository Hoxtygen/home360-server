/* (C)2024-2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.*;
import java.util.List;

/**
 * @author Wasiu Idowu
 *
 * */
public interface ListingService {
  Listing createListing(ListingDTO request);

  Object deleteListing(String listingId);

  PaginatedListingResponse getFilteredListings(
      int page, int size, String city, int annualRent, String apartmentType);

  ListingWithAgentInfo getListingById(String listingId);

  List<ListingWithViewCountDTO> allListings();

  PaginatedListingResponse getListingsByAgentId(int page, int size, int agentId);

  Listing updateRentedListing(RentUpdate update);

  Listing findListingById(String listingId);
}
