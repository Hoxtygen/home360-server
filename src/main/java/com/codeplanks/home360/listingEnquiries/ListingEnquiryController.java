package com.codeplanks.home360.listingEnquiries;


import com.codeplanks.home360.event.ListingEnquiryEvent;
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
  private final ListingEnquiryService listingEnquiryService;
  private final ApplicationEventPublisher eventPublisher;

  @PostMapping("/listing-enquiry")
  public ResponseEntity<SuccessDataResponse<ListingEnquiryDTO>> createEnquiry(
          @RequestBody @Validated ListingEnquiry enquiry) {
    System.out.println("controller layer: " + enquiry);
    SuccessDataResponse<ListingEnquiryDTO> newListingEnquiry = new SuccessDataResponse<>();
    newListingEnquiry.setData(listingEnquiryService.makeEnquiry(enquiry));
    newListingEnquiry.setMessage("Listing enquiry created successfully");
    newListingEnquiry.setStatus(HttpStatus.CREATED);
    eventPublisher.publishEvent(new ListingEnquiryEvent(enquiry.getEmail(), enquiry.getListingId()));
    return new ResponseEntity<>(newListingEnquiry, HttpStatus.CREATED);
  }

}
