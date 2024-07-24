package com.codeplanks.home360.listing;

import com.codeplanks.home360.utils.SuccessDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Wasiu Idowu
 *
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ListingController {
  private final ListingService listingService;

  @PostMapping("/listing")
  public ResponseEntity<SuccessDataResponse<ListingDTO>> createListing(
          @RequestBody Listing request) {
    SuccessDataResponse<ListingDTO> newListing = new SuccessDataResponse<>();
    newListing.setData(listingService.createListing(request));
    newListing.setMessage("Listing created successfully");
    newListing.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(newListing, HttpStatus.CREATED);
  }

  @GetMapping("/listings")
  public ResponseEntity<SuccessDataResponse<List<Listing>>> getAllListings() {
    SuccessDataResponse<List<Listing>> allListings = new SuccessDataResponse<>();
    allListings.setData(listingService.allListings());
    allListings.setMessage("Listings retrieved successfully");
    allListings.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(allListings, HttpStatus.OK);
  }


  @DeleteMapping("/listings/{listingId}")
  public ResponseEntity<?> deleteListing(@PathVariable String listingId) {
    SuccessDataResponse<Object> response = new SuccessDataResponse<>();
    response.setData(listingService.deleteListing(listingId));
    response.setMessage("Listing deleted successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/listings/{listingId}")
  public ResponseEntity<SuccessDataResponse<ListingWithAgentInfo>> getListing(@PathVariable String listingId) {
    SuccessDataResponse<ListingWithAgentInfo> response = new SuccessDataResponse<>();
    response.setData(listingService.getListingById(listingId));
    response.setMessage("Listing fetched successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/listings/search")
  public ResponseEntity<SuccessDataResponse<PaginatedResponse<Listing>>> getFilteredListings(
          @RequestParam(value = "page", defaultValue = "1") int page,
          @RequestParam(value = "size", defaultValue = "25") int size,
          @RequestParam(required = false) String city,
          @RequestParam(required = false, defaultValue = "0") int annualRent,
          @RequestParam(required = false) String apartmentType
  ) {
    SuccessDataResponse<PaginatedResponse<Listing>
            > response = new SuccessDataResponse<>();
    response.setData(listingService.getFilteredListings(page - 1, size, city, annualRent,
            apartmentType));
    response.setMessage("Listing fetched successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/userListings")
  public ResponseEntity<SuccessDataResponse<PaginatedResponse<Listing>>> getAgentListings(
          @RequestParam(value = "page", defaultValue = "1") int page,
          @RequestParam(value = "size", defaultValue = "25") int size
  ) {
    SuccessDataResponse<PaginatedResponse<Listing>> agentListings = new SuccessDataResponse<>();
    agentListings.setData(listingService.getListingsByAgentId(page - 1, size));
    agentListings.setMessage("Listings retrieved successfully");
    agentListings.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(agentListings, HttpStatus.OK);
  }
  @PatchMapping("/listing")
  public ResponseEntity<SuccessDataResponse<ListingDTO>> updateTakenListing(@RequestBody @Validated RentUpdate updateRequest) {
    SuccessDataResponse<ListingDTO> updatedListing = new SuccessDataResponse<>();
    updatedListing.setData(listingService.updateRentedListing(updateRequest));
    updatedListing.setMessage("Listing updated successfully");
    updatedListing.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }
}
