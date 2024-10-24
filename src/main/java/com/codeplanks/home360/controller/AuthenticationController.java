/* (C)2024 */
package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.service.AuthenticationServiceImpl;
import com.codeplanks.home360.service.RefreshTokenServiceImpl;
import com.codeplanks.home360.service.VerificationTokenServiceImpl;
import com.codeplanks.home360.utils.SuccessDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wasiu Idowu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication management APIs")
public class AuthenticationController {
  private final AuthenticationServiceImpl authenticationServiceImpl;
  private final RegistrationCompleteEventListener eventListener;
  private final RefreshTokenServiceImpl refreshTokenService;
  private final VerificationTokenServiceImpl verificationTokenService;

  @Operation(
      summary = "Register user",
      description = "Register a new user to use the application",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Created successfully",
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
        responseCode = "409",
        description = "conflict",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
  })
  @PostMapping("/register")
  public ResponseEntity<SuccessDataResponse<String>> register(
      @RequestBody @Valid RegisterRequest request) {
    SuccessDataResponse<String> newUser = new SuccessDataResponse<>();
    AppUser response = authenticationServiceImpl.register(request);
    newUser.setData("Registration Successful. A verification link have been sent to your email.");
    newUser.setMessage("User registration successful");
    newUser.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(newUser, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Login user",
      description = "Sign in a user to the application",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Sign in successful",
        content = {
          @Content(
              schema = @Schema(implementation = AuthenticationResponse.class),
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
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
    return new ResponseEntity<>(authenticationServiceImpl.login(request), HttpStatus.OK);
  }

  @Operation(
      summary = "Verify user email",
      description = "Verifies a new user email address",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = " Verification successful",
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
        })
  })
  @GetMapping("/verifyEmail")
  public ResponseEntity<SuccessDataResponse<String>> verifyEmail(
      @RequestParam("token") String token) {
    SuccessDataResponse<String> response =
        new SuccessDataResponse<>(
            HttpStatus.OK, "Success", authenticationServiceImpl.verifyAccount(token));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Resend verification token",
      description = "Resend  verification token to verify new user email address",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = " Verification token sent",
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
        description = "Token not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @GetMapping("/resend-verification-token")
  public ResponseEntity<SuccessDataResponse<String>> resendVerificationToken(
      @RequestParam("token") String oldToken)
      throws MessagingException, UnsupportedEncodingException {
    SuccessDataResponse<String> response =
        new SuccessDataResponse<>(
            HttpStatus.OK, "Success", verificationTokenService.resendVerificationToken(oldToken));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Generate access token",
      description = "Generates a new access token",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Access token generated Successfully",
        content = {
          @Content(
              schema = @Schema(implementation = TokenResponse.class),
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
        description = "Refresh token not found",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        })
  })
  @PostMapping("/refreshToken")
  public ResponseEntity<SuccessDataResponse<TokenResponse>> getRefreshToken(
      @RequestBody TokenRequest tokenRequest) {
    SuccessDataResponse<TokenResponse> response = new SuccessDataResponse<>();
    response.setData(refreshTokenService.refreshToken(tokenRequest));
    response.setMessage("Success");
    response.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Request for password reset",
      description = "User request for password reset",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Successful",
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
  @PostMapping("/password-reset-request")
  public ResponseEntity<SuccessDataResponse<String>> resetPasswordRequest(
      @RequestBody PasswordResetRequest passwordRequest)
      throws MessagingException, UnsupportedEncodingException {
    SuccessDataResponse<String> response = new SuccessDataResponse<>();
    response.setData(authenticationServiceImpl.requestPasswordReset(passwordRequest.getEmail()));
    response.setMessage("Success");
    response.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Reset forgotten user password",
      description = "Resets the password of users that forgot their password",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Password reset successful",
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
  @PostMapping("/reset-password")
  public ResponseEntity<SuccessDataResponse<String>> resetPassword(
      @RequestBody @Valid PasswordResetRequest passwordResetRequest,
      @RequestParam("token") String token) {
    SuccessDataResponse<String> response =
        new SuccessDataResponse<>(
            HttpStatus.CREATED,
            "Success",
            authenticationServiceImpl.resetForgottenUserPassword(passwordResetRequest, token));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
