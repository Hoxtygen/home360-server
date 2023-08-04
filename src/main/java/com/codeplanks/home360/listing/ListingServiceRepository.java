package com.codeplanks.home360.listing;

public interface ListingServiceRepository {
  ListingDTO createListing(Listing request);
  Object deleteListing(String listingId);
}
