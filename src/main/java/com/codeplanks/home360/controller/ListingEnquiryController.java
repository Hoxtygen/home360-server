/* (C)2024 */
package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryDTO;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReply;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReplyDTO;
import com.codeplanks.home360.event.ListingEnquiryEvent;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.service.ListingEnquiryServiceImpl;
import com.codeplanks.home360.utils.SuccessDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Listing Enquiries", description = "Listing enquiries management APIs")
public class ListingEnquiryController {
  private final ListingEnquiryServiceImpl listingEnquiryService;
  private final ApplicationEventPublisher eventPublisher;

  @Operation(
      summary = "Create a listing enquiry",
      description = "Create a listing enquiry",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Created successfully",
        content = {
          @Content(
              schema = @Schema(implementation = ListingEnquiry.class),
              mediaType = "application/json")
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
        })
  })
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

  @Operation(
      summary = "Get all listing enquiries of an agent",
      description = "Returns all listing enquiries of a given agent",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved",
        content = {
          @Content(
              schema = @Schema(implementation = ListingEnquiry.class),
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
  @GetMapping("/listing-enquiries")
  public ResponseEntity<SuccessDataResponse<PaginatedResponse<ListingEnquiry>>> getListingEnquiries(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "25") int size,
      @RequestParam(required = false) Integer senderId) {
    SuccessDataResponse<PaginatedResponse<ListingEnquiry>> listingEnquiries =
        new SuccessDataResponse<>();
    listingEnquiries.setData(listingEnquiryService.getListingEnquiries(page - 1, size, senderId));
    listingEnquiries.setMessage("Listing enquiries retrieved successfully");
    listingEnquiries.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(listingEnquiries, HttpStatus.OK);
  }

  @Operation(
      summary = "Get listing enquiry by Id",
      description = "Returns a listing enquiry by specifying the Id",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved",
        content = {
          @Content(
              schema = @Schema(implementation = ListingEnquiry.class),
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
    @ApiResponse(
        responseCode = "404",
        description = "Not Found - The listing enquiry was not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @GetMapping("/listing-enquiries/{listingEnquiryId}")
  public ResponseEntity<SuccessDataResponse<ListingEnquiry>> getAgentListingEnquiryById(
      @PathVariable("listingEnquiryId")
          @Parameter(
              name = "listingEnquiryId",
              description = "Listing enquiry Id",
              example = "66ac91c8cb3294535d04e0e3")
          String listingEnquiryId) {
    SuccessDataResponse<ListingEnquiry> response = new SuccessDataResponse<>();
    response.setData(listingEnquiryService.getListingEnquiryById(listingEnquiryId));
    response.setMessage("Listing enquiry fetched successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Set listing enquiry to read",
      description = "Set a given listing enquiry to read",
      tags = {"PATCH"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully set to read",
        content = {
          @Content(
              schema = @Schema(implementation = ListingEnquiry.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "304",
        description = "Not modified. Message already set to read.",
        content = {
          @Content(
              schema = @Schema(implementation = ListingEnquiry.class),
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
  @PatchMapping("listing-enquiry/{listingEnquiryId}/read")
  public ResponseEntity<SuccessDataResponse<Boolean>> markMessageAsRead(
      @PathVariable String listingEnquiryId) {
    SuccessDataResponse<Boolean> response = new SuccessDataResponse<>();
    response.setData(listingEnquiryService.markMessageAsRead(listingEnquiryId));
    if (!response.getData()) {
      response.setMessage("Listing enquiry message already  read");
      response.setStatus(HttpStatus.NOT_MODIFIED);
    } else {
      response.setMessage("Listing enquiry message marked as read");
      response.setStatus(HttpStatus.OK);
    }
    return new ResponseEntity<>(response, response.getStatus());
  }

  @Operation(
      summary = "Sends messages between registered users",
      description =
          "Allow registered users to send messages between themselves in response "
              + "to listing enquiries",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Created successfully",
        content = {
          @Content(
              schema = @Schema(implementation = ListingEnquiryMessageReply.class),
              mediaType = "application/json")
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
        responseCode = "404",
        description = "Not Found - The listing enquiry ID, sender or receiver was " + "not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @PostMapping("/listing-enquiry/{enquiryId}/message")
  public ResponseEntity<SuccessDataResponse<ListingEnquiryMessageReply>> sendEnquiryReply(
      @PathVariable String enquiryId,
      @RequestBody @Valid ListingEnquiryMessageReplyDTO replyMessage) {
    SuccessDataResponse<ListingEnquiryMessageReply> response = new SuccessDataResponse<>();
    response.setData(listingEnquiryService.addReplyMessage(enquiryId, replyMessage));
    response.setStatus(HttpStatus.CREATED);
    response.setMessage("message posted.");
    return new ResponseEntity<>(response, response.getStatus());
  }
}
