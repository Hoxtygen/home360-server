/* (C)2025 */
package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.rental.Rental;
import com.codeplanks.home360.domain.rental.RentalDTO;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.service.RentalServiceImpl;
import com.codeplanks.home360.utils.SuccessDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rentals")
@Tag(name = "Rental", description = "Rent management APIs")
public class RentalController {
  private final RentalServiceImpl rentalService;

  @Operation(
      summary = "Creates rent record",
      description = "Creates rent information record",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Created successfully",
        content = {
          @Content(schema = @Schema(implementation = Rental.class), mediaType = "application/json")
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
        description = "Not authorized to perform this operation",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "404",
        description = "Renter does not exist",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
  })
  @PostMapping
  public ResponseEntity<SuccessDataResponse<Rental>> saveRentalInfo(
      @RequestBody @Valid RentalDTO rentalRequest) {
    SuccessDataResponse<Rental> response = new SuccessDataResponse<>();
    response.setData(rentalService.saveRentDetails(rentalRequest));
    response.setStatus(HttpStatus.CREATED);
    response.setMessage("Rent information created successfully");
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
