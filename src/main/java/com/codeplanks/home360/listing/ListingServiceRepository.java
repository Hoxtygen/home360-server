package com.codeplanks.home360.listing;

import com.codeplanks.home360.listing.listingDtos.LandDTO;

public interface ListingServiceRepository {
  ListingDTO createListing(Listing request);
  Object deleteListing(String listingId);
  LandDTO createLandListing(LandListing request);
}
