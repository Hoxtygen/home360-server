/* (C)2024 */
package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.listing.*;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.service.ListingService;
import com.codeplanks.home360.utils.SuccessDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wasiu Idowu
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Listing", description = "Listings management APIs")
public class ListingController {
  private final ListingService listingService;

  @Operation(
      summary = "Create a listing",
      description = "Creates a new listing",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Created successfully",
        content = {
          @Content(schema = @Schema(implementation = Listing.class), mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @PostMapping("/listing")
  public ResponseEntity<SuccessDataResponse<ListingDTO>> createListing(
      @RequestBody Listing request) {
    SuccessDataResponse<ListingDTO> newListing = new SuccessDataResponse<>();
    newListing.setData(listingService.createListing(request));
    newListing.setMessage("Listing created successfully");
    newListing.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(newListing, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Get all listings",
      description = "Returns all listings",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved",
        content = {@Content(mediaType = "application/json")}),
  })
  @GetMapping("/listings")
  public ResponseEntity<SuccessDataResponse<List<Listing>>> getAllListings() {
    SuccessDataResponse<List<Listing>> allListings = new SuccessDataResponse<>();
    allListings.setData(listingService.allListings());
    allListings.setMessage("Listings retrieved successfully");
    allListings.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(allListings, HttpStatus.OK);
  }

  @Operation(
      summary = "Delete listing ",
      description = "Delete a given listing by ID",
      tags = {"DELETE"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = {
          @Content(
              schema = @Schema(implementation = SuccessDataResponse.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - Not allowed to delete",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "404",
        description = "Not Found - Listing enquiry with the given ID was not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @DeleteMapping("/listings/{listingId}")
  public ResponseEntity<?> deleteListing(@PathVariable String listingId) {
    SuccessDataResponse<Object> response = new SuccessDataResponse<>();
    response.setData(listingService.deleteListing(listingId));
    response.setMessage("Listing deleted successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Get listing by Id",
      description = "Returns a listing by specifying the Id",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved",
        content = {
          @Content(schema = @Schema(implementation = Listing.class), mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden.",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "404",
        description = "Not Found - The listing was not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @GetMapping("/listings/{listingId}")
  public ResponseEntity<SuccessDataResponse<ListingWithAgentInfo>> getListing(
      @PathVariable String listingId) {
    SuccessDataResponse<ListingWithAgentInfo> response = new SuccessDataResponse<>();
    response.setData(listingService.getListingById(listingId));
    response.setMessage("Listing fetched successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Get listings by searching",
      description = "Return listings by specifying search parameters",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved",
        content = {
          @Content(
              schema = @Schema(implementation = PaginatedResponse.class),
              mediaType = "application/json")
        }),
  })
  @Parameters({
    @Parameter(name = "page", description = "Page Number starting from 1"),
    @Parameter(name = "size", description = "Number of items per page"),
    @Parameter(name = "city", description = "The city the search is to be based on"),
    @Parameter(name = "annualRent", description = "The minimum annual rent"),
    @Parameter(name = "apartmentType", description = "The type of apartment you want")
  })
  @GetMapping("/listings/search")
  public ResponseEntity<SuccessDataResponse<PaginatedResponse<Listing>>> getFilteredListings(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "25") int size,
      @RequestParam(required = false) String city,
      @RequestParam(required = false, defaultValue = "0") int annualRent,
      @RequestParam(required = false) String apartmentType) {
    SuccessDataResponse<PaginatedResponse<Listing>> response = new SuccessDataResponse<>();
    response.setData(
        listingService.getFilteredListings(page - 1, size, city, annualRent, apartmentType));
    response.setMessage("Listing fetched successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Get listings by agent ID",
      description = "Return listings by specifying the agent ID",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved",
        content = {
          @Content(
              schema = @Schema(implementation = PaginatedResponse.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden.",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
  })
  @GetMapping("/userListings")
  public ResponseEntity<SuccessDataResponse<PaginatedResponse<Listing>>> getAgentListings(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "25") int size) {
    SuccessDataResponse<PaginatedResponse<Listing>> agentListings = new SuccessDataResponse<>();
    agentListings.setData(listingService.getListingsByAgentId(page - 1, size));
    agentListings.setMessage("Listings retrieved successfully");
    agentListings.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(agentListings, HttpStatus.OK);
  }

  @Operation(
      summary = "Set listing  to taken",
      description = "Set a given listing to taken",
      tags = {"PATCH"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = {
          @Content(schema = @Schema(implementation = Listing.class), mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "304",
        description = "Not modified. Message already set to rented.",
        content = {
          @Content(schema = @Schema(implementation = Listing.class), mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "401",
        description = "Authentication required",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - Not allowed to make update",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "404",
        description = "Not Found - Listing enquiry with the given ID was not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @PatchMapping("/listing")
  public ResponseEntity<SuccessDataResponse<ListingDTO>> updateTakenListing(
      @RequestBody @Validated RentUpdate updateRequest) {
    SuccessDataResponse<ListingDTO> updatedListing = new SuccessDataResponse<>();
    updatedListing.setData(listingService.updateRentedListing(updateRequest));
    updatedListing.setMessage("Listing updated successfully");
    updatedListing.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(updatedListing, HttpStatus.OK);
  }
}
