package com.codeplanks.home360.listing.listingMappers;

import com.codeplanks.home360.listing.Home;
import com.codeplanks.home360.listing.listingDtos.HomeDTO;

public class HomeListingDtoMapper {
  public  static HomeDTO mapToHomeListingDTO(Home homeListing) {
    return new HomeDTO(
            homeListing.getId(),
            homeListing.getTitle(),
            homeListing.getDescription(),
            homeListing.getPosition(),
            homeListing.getMiscellaneous(),
            homeListing.getAddress(),
            homeListing.isAvailable(),
            homeListing.getAgentId(),
            homeListing.getPropertyImages(),
            homeListing.getCreatedAt(),
            homeListing.getUpdatedAt(),
            homeListing.getType(),
            homeListing.getProperties()
    );
  }
}
