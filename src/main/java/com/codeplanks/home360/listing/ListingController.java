package com.codeplanks.home360.listing;

import com.codeplanks.home360.utils.SuccessDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ListingController {
  private final ListingService listingService;

  @PostMapping("/listing")
  public ResponseEntity<SuccessDataResponse<ListingDTO>> createListing(
          @RequestBody @Validated Listing request) {
    SuccessDataResponse<ListingDTO> newListing = new SuccessDataResponse<>();
    newListing.setData(listingService.createListing(request));
    newListing.setMessage("Listing created successfully");
    newListing.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(newListing, HttpStatus.CREATED);
  }

}
