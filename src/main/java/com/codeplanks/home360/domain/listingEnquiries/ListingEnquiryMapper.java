package com.codeplanks.home360.domain.listingEnquiries;

public class ListingEnquiryMapper {
  public static ListingEnquiryDTO mapToListingEnquiryDTO(ListingEnquiry listingEnquiry) {
    return new ListingEnquiryDTO(
            listingEnquiry.getId(),
            listingEnquiry.getFirstName(),
            listingEnquiry.getLastName(),
            listingEnquiry.getEmail(),
            listingEnquiry.getPhoneNumber(),
            listingEnquiry.getLocation(),
            listingEnquiry.getMessage(),
            listingEnquiry.getSalutation(),
            listingEnquiry.getEmploymentStatus(),
            listingEnquiry.getPets(),
            listingEnquiry.getCommercialPurpose(),
            listingEnquiry.getListingId(),
            listingEnquiry.getAgentId(),
            listingEnquiry.getCreatedAt()
    );
  }

}
