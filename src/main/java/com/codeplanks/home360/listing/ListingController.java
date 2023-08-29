package com.codeplanks.home360.listing;

import com.codeplanks.home360.utils.SuccessDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

  @GetMapping("/listings")
  public ResponseEntity<SuccessDataResponse<List<Listing>>> getAllListings() {
    SuccessDataResponse<List<Listing>> allListings = new SuccessDataResponse<>();
    allListings.setMessage("Listings retrieved successfully");
    allListings.setStatus(HttpStatus.OK);
    allListings.setData(listingService.allListings());
    return new ResponseEntity<>(allListings, HttpStatus.OK);
  }


  @DeleteMapping("/listings/{listingId}")
  public ResponseEntity<?> deleteListing(@PathVariable String listingId) {
    SuccessDataResponse<Object> response = new SuccessDataResponse<>();
    response.setMessage("Listing deleted successfully");
    response.setStatus(HttpStatus.OK);
    response.setData(listingService.deleteListing(listingId));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/listings/{listingId}")
  public ResponseEntity<SuccessDataResponse<Listing>> getListing(@PathVariable String listingId){
    SuccessDataResponse<Listing> response = new SuccessDataResponse<>();
    response.setMessage("Listing fetched successfully");
    response.setStatus(HttpStatus.OK);
    response.setData(listingService.getListingById(listingId));
    return  new ResponseEntity<>(response, HttpStatus.OK);
  }

}
