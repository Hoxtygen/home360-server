package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.listingView.ListingView;
import com.codeplanks.home360.domain.listingView.ListingViewDTO;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.service.ListingViewServiceImpl;
import com.codeplanks.home360.utils.SuccessDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/listing-views")
@Tag(name = "Listing Views", description = "Listing Views management APIs")
public class ListingViewController {
  private final ListingViewServiceImpl listingViewService;

  @Operation(
      summary = "Create listing views",
      description = "Create a new listing view and save it to the database",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Created successfully",
        content = {
          @Content(
              schema = @Schema(implementation = ListingView.class),
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
  })
  @PostMapping
  public ResponseEntity<SuccessDataResponse<ListingView>> createListingView(
      @RequestBody @Validated ListingViewDTO request) {
    SuccessDataResponse<ListingView> response = new SuccessDataResponse<>();
    response.setData(listingViewService.saveListingView(request));
    response.setMessage("Views created");
    response.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(response, response.getStatus());
  }
}

