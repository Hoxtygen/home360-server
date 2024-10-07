/* (C)2024 */
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

  PaginatedResponse<Listing> getFilteredListings(
      int page, int size, String city, int annualRent, String apartmentType);

  ListingWithAgentInfo getListingById(String listingId);

  List<Listing> allListings();

  PaginatedResponse<Listing> getListingsByAgentId(int page, int size);

  ListingDTO updateRentedListing(RentUpdate update);
}
