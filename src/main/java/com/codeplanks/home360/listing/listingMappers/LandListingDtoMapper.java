package com.codeplanks.home360.listing.listingMappers;

import com.codeplanks.home360.listing.LandListing;
import com.codeplanks.home360.listing.listingDtos.LandDTO;

public class LandListingDtoMapper {
  public static LandDTO mapToLandListingDTO(LandListing landListing) {
    return new LandDTO(
            landListing.getId(),
            landListing.getTitle(),
            landListing.getDescription(),
            landListing.getPosition(),
            landListing.getMiscellaneous(),
            landListing.getAddress(),
            landListing.isAvailable(),
            landListing.getAgentId(),
            landListing.getPropertyImages(),
            landListing.getCreatedAt(),
            landListing.getUpdatedAt(),
            landListing.getType(),
            landListing.getProperties()

    );
  }
}
