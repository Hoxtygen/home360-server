/* (C)2024-2025 */
package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.listingEnquiries.*;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.event.ListingEnquiryEvent;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.service.EnquiryMessageService;
import com.codeplanks.home360.service.ListingEnquiryService;
import com.codeplanks.home360.service.UserService;
import com.codeplanks.home360.utils.SuccessDataResponse;
import com.codeplanks.home360.validation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/listing-enquiries")
@Tag(name = "Listing Enquiries", description = "Listing enquiries management APIs")
public class ListingEnquiryController {
  private final ListingEnquiryService listingEnquiryService;
  private final EnquiryMessageService enquiryMessageService;
  private final ApplicationEventPublisher eventPublisher;
  private final UserService userService;

  private static  final Logger logger = LoggerFactory.getLogger(ListingEnquiryController.class);

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
  @PostMapping
  public ResponseEntity<SuccessDataResponse<ListingEnquiry>> createEnquiry(
      @RequestBody @Validated ListingEnquiryDTO enquiry) {
    SuccessDataResponse<ListingEnquiry> newListingEnquiry = new SuccessDataResponse<>();
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
  @GetMapping
  public ResponseEntity<SuccessDataResponse<PaginatedListingEnquiriesResponse>> getListingEnquiries(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "25") int size,
      @RequestParam(required = false) Integer senderId) {
    Integer agentId = userService.extractUserId();
    SuccessDataResponse<PaginatedListingEnquiriesResponse> listingEnquiries =
        new SuccessDataResponse<>();
    listingEnquiries.setData(
        listingEnquiryService.getListingEnquiries(page - 1, size, senderId, agentId));
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
  @GetMapping("/{listingEnquiryId}")
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
  @PatchMapping("/{listingEnquiryId}/read")
  public ResponseEntity<SuccessDataResponse<Boolean>> markMessageAsRead(
      @PathVariable String listingEnquiryId) {
    SuccessDataResponse<Boolean> response = new SuccessDataResponse<>();
    response.setData(listingEnquiryService.markEnquiryAsRead(listingEnquiryId));
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
              schema = @Schema(implementation = EnquiryMessage.class),
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
  @MessageMapping("/chat/{enquiryId}/sendMessage")
  @SendTo("/topic/public.{enquiryId}")
  public EnquiryMessage sendEnquiryReply(
      @DestinationVariable String enquiryId,
      @Payload ListingEnquiryMessageReplyDTO replyMessage,
      @CurrentUser AppUser currentUser) {
    return enquiryMessageService.addReplyMessage(enquiryId, replyMessage, currentUser.getId());
  }

  @Operation(
      summary = "Get enquiry message history",
      description = "Returns paginated history of messages for a given enquiry",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
  })
  @GetMapping("/{enquiryId}/messages")
  public ResponseEntity<SuccessDataResponse<PaginatedListingEnquiriesChat>> getEnquiryHistory(
      @PathVariable String enquiryId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    SuccessDataResponse<PaginatedListingEnquiriesChat> response = new SuccessDataResponse<>();
    response.setData(enquiryMessageService.getEnquiryMessages(enquiryId, page - 1, size));
    response.setMessage("Messages retrieved successfully");
    response.setStatus(HttpStatus.OK);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @MessageExceptionHandler
  @SendToUser("/queue/errors")
  public ApiError handleException(Throwable exception) {
    logger.error("WebSocket Error: {}", exception.getMessage());
    return new ApiError(
        ZonedDateTime.now(ZoneOffset.UTC), HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @Operation(
      summary = "Get enquiries by listing ID",
      description = "Gets all listing enquiries made to a given listing",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful",
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
        description = "Not authorized to perform this operation",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "404",
        description = "Listing not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
  })
  @GetMapping("/listing/{listingId}")
  public ResponseEntity<SuccessDataResponse<List<ListingEnquiry>>> getEnquiriesByListingId(
      @PathVariable String listingId) {
    SuccessDataResponse<List<ListingEnquiry>> response = new SuccessDataResponse<>();
    response.setData(listingEnquiryService.getEnquiriesByListingId(listingId));
    response.setStatus(HttpStatus.OK);
    response.setMessage("Enquiries fetched successfully");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
