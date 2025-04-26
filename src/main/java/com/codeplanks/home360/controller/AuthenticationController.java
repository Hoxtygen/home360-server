/* (C)2024-2025 */
package com.codeplanks.home360.controller;

import com.blueconic.browscap.ParseException;
import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.service.AuthenticationServiceImpl;
import com.codeplanks.home360.service.RefreshTokenServiceImpl;
import com.codeplanks.home360.service.VerificationTokenServiceImpl;
import com.codeplanks.home360.utils.AuthenticationUtils;
import com.codeplanks.home360.utils.SessionUserInfoUtil;
import com.codeplanks.home360.utils.SuccessDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
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
  private final RefreshTokenServiceImpl refreshTokenService;
  private final VerificationTokenServiceImpl verificationTokenService;
  private final AuthenticationUtils authenticationUtils;

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
    authenticationServiceImpl.register(request);
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
  public ResponseEntity<SuccessDataResponse<AuthenticationResponse>> login(
      @Valid @RequestBody AuthenticationRequest request,
      HttpServletRequest httpServletRequest,
      HttpServletResponse response)
      throws IOException, ParseException {

    SessionUserInfo sessionUserInfo =
        SessionUserInfoUtil.extractSessionUserInfo(httpServletRequest);

    SuccessDataResponse<AuthenticationResponse> result = new SuccessDataResponse<>();
    AuthenticationResponse authResponse = authenticationServiceImpl.login(request, sessionUserInfo);

    // Create and set session token cookie
    Cookie sessionTokenCookie =
        new Cookie("sessionToken", authResponse.getToken().getSessionToken());
    sessionTokenCookie.setHttpOnly(true);
    sessionTokenCookie.setSecure(!authenticationUtils.isLocalEnvironment());
    sessionTokenCookie.setPath("/");
    sessionTokenCookie.setMaxAge(60 * 60 * 24 * 3); // 3 days
    response.addCookie(sessionTokenCookie);

    Cookie refreshTokenCookie =
        new Cookie("refreshToken", authResponse.getToken().getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(!authenticationUtils.isLocalEnvironment());
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(60 * 60 * 24 * 3);

    response.addCookie(refreshTokenCookie);

    result.setData(authResponse);
    result.setMessage("Login successful");
    result.setStatus(HttpStatus.OK);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(
      summary = "Log user out",
      description = "Sign a user out of the application",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Logout successful",
        content = {
          @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
        }),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = {
          @Content(
              schema = @Schema(implementation = ApiError.class),
              mediaType = "application/json")
        }),
  })
  @PostMapping("/logout")
  public ResponseEntity<SuccessDataResponse<String>> logout(
      @RequestHeader("Authorization") String authToken,
      HttpServletRequest request,
      HttpServletResponse response,
      @CookieValue("sessionToken") String sessionToken) {
    String token = authToken.substring(7);

    SuccessDataResponse<String> result = new SuccessDataResponse<>();

    result.setData(authenticationServiceImpl.logout(token, request, response, sessionToken));
    result.setStatus(HttpStatus.OK);
    result.setMessage("Logout successful");

    return new ResponseEntity<>(result, HttpStatus.OK);
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
      HttpServletRequest request, HttpServletResponse response) {
    SuccessDataResponse<TokenResponse> result = new SuccessDataResponse<>();

    String refreshToken = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }

    if (refreshToken == null) {
      throw new UnAuthorizedException("Refresh token is missing");
    }

    TokenRequest tokenRequest = new TokenRequest();
    tokenRequest.setToken(refreshToken);
    TokenResponse tokenResponse = refreshTokenService.refreshToken(tokenRequest);

    Cookie refreshTokenCookie = new Cookie("refreshToken", tokenRequest.getToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(!authenticationUtils.isLocalEnvironment());
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(60 * 60 * 24 * 3);

    response.addCookie(refreshTokenCookie);

    result.setData(tokenResponse);
    result.setMessage("Success");
    result.setStatus(HttpStatus.CREATED);

    return new ResponseEntity<>(result, HttpStatus.CREATED);
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
      @RequestBody @Valid PasswordResetRequestDTO passwordResetRequest)
      throws MessagingException, UnsupportedEncodingException {
    SuccessDataResponse<String> response = new SuccessDataResponse<>();
    response.setData(authenticationServiceImpl.requestPasswordReset(passwordResetRequest));
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
      @RequestBody @Valid PasswordUpdateDTO passwordUpdateDTO,
      @RequestParam("token") String token) {
    SuccessDataResponse<String> response =
        new SuccessDataResponse<>(
            HttpStatus.CREATED,
            "Success",
            authenticationServiceImpl.resetForgottenUserPassword(passwordUpdateDTO, token));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
