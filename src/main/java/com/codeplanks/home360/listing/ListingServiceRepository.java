package com.codeplanks.home360.listing;

import java.util.List;

/**
 * @author Wasiu Idowu
 *
 * */

public interface ListingServiceRepository {
  ListingDTO createListing(Listing request);

  Object deleteListing(String listingId);

  PaginatedResponse<Listing> getFilteredListings(
          int page,
          int size,
          String city,
          int annualRent,
          String apartmentType
  );

  Listing getListingById(String listingId);

  List<Listing> allListings();

  PaginatedResponse<Listing> getListingsByAgentId(int page, int size);
  ListingDTO updateRentedListing(RentUpdate update);

}
