package com.codeplanks.home360.listing.listingMappers;

import com.codeplanks.home360.listing.Listing;
import com.codeplanks.home360.listing.ListingDTO;

public class ListingMapper {

  public static ListingDTO mapToListingDTO(Listing listing) {
    return new ListingDTO(
            listing.getId(),
            listing.getTitle(),
            listing.getAgentId(),
            listing.getDescription(),
            listing.getFurnishing(),
            listing.getPosition(),
            listing.getMiscellaneous(),
            listing.getAddress(),
            listing.isAvailable(),
            listing.getAvailableFrom(),
            listing.getCost(),
            listing.getDetails(),
            listing.getFacilityQuality(),
            listing.getPetsAllowed(),
            listing.getApartmentInfo(),
            listing.getApplicationDocs(),
            listing.getApartmentImages(),
            listing.getCreatedAt(),
            listing.getUpdatedAt()
    );

  }

}
