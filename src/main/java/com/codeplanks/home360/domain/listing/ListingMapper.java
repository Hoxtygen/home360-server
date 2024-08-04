package com.codeplanks.home360.domain.listing;

import com.codeplanks.home360.domain.user.AppUser;

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
            listing.isDraft(),
            listing.isRented(),
            listing.getAvailableFrom(),
            listing.getCost(),
            listing.getDetails(),
            listing.getFacilityQuality(),
            listing.getPetsAllowed(),
            listing.getApartmentInfo(),
            listing.getApplicationDocs(),
            listing.getApartmentImages(),
            listing.getCreatedAt(),
            listing.getUpdatedAt(),
            listing.getDraftDate(),
            listing.getRentDate()
    );

  }

  public static ListingAgentInfo mapToListingAgentInfo(AppUser user){
    return ListingAgentInfo.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .build();
  }

}
