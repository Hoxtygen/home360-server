package com.codeplanks.home360.controller;


import com.codeplanks.home360.event.ListingEnquiryEvent;
import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryDTO;
import com.codeplanks.home360.service.ListingEnquiryServiceImpl;
import com.codeplanks.home360.utils.SuccessDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ListingEnquiryController {
  private final ListingEnquiryServiceImpl listingEnquiryService;
  private final ApplicationEventPublisher eventPublisher;

  @PostMapping("/listing-enquiry")
  public ResponseEntity<SuccessDataResponse<ListingEnquiryDTO>> createEnquiry(
          @RequestBody @Validated ListingEnquiry enquiry) {
    SuccessDataResponse<ListingEnquiryDTO> newListingEnquiry = new SuccessDataResponse<>();
    newListingEnquiry.setData(listingEnquiryService.makeEnquiry(enquiry));
    newListingEnquiry.setMessage("Listing enquiry created successfully");
    newListingEnquiry.setStatus(HttpStatus.CREATED);
    eventPublisher.publishEvent(
            new ListingEnquiryEvent(enquiry.getEmail(), enquiry.getListingId()));
    return new ResponseEntity<>(newListingEnquiry, HttpStatus.CREATED);
  }

@GetMapping("/listing-enquiry")
  public ResponseEntity<SuccessDataResponse<PaginatedResponse<ListingEnquiry>>> getAgentListingEnquiry(
          @RequestParam(value = "page", defaultValue = "1") int page,
          @RequestParam(value = "size", defaultValue = "25") int size
  ) {
    SuccessDataResponse<PaginatedResponse<ListingEnquiry>> agentListingEnquiries =
            new SuccessDataResponse<>();
    agentListingEnquiries.setData(
            listingEnquiryService.getListingEnquiriesByAgentId(page - 1, size));
    agentListingEnquiries.setMessage("Listing enquiries retrieved successfully");
    agentListingEnquiries.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(agentListingEnquiries, HttpStatus.OK);
  }

}
