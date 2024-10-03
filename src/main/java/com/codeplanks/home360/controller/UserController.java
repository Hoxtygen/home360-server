/* (C)2024 */
package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.auth.PasswordChangeRequest;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.AppUserDTO;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.service.UserServiceImpl;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User management APIs")
public class UserController {
  private final UserServiceImpl userService;

  @Operation(
      summary = "Change user password",
      description = "Changes the password of users who wants to knowingly change it.",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Password change successful",
        content = {
          @Content(
              schema = @Schema(implementation = SuccessDataResponse.class),
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
        responseCode = "404",
        description = "User not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @PostMapping("/change-password")
  public ResponseEntity<SuccessDataResponse<String>> changePassword(
      @RequestBody @Valid PasswordChangeRequest request) {
    SuccessDataResponse<String> response =
        new SuccessDataResponse<>(
            HttpStatus.OK, "Success", userService.changeUserPassword(request));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Gets user details",
      description = "It retrieves the details of a logged-in user",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = {
          @Content(
              schema = @Schema(implementation = AppUserDTO.class),
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
        description = "User not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @GetMapping("/user-details")
  public ResponseEntity<SuccessDataResponse<AppUserDTO>> getUserDetails() {
    SuccessDataResponse<AppUserDTO> response =
        new SuccessDataResponse<>(
            HttpStatus.OK, "User details retrieved successfully", userService.getUserDetails());
    return new ResponseEntity<>(response, response.getStatus());
  }
}
