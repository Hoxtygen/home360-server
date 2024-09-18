/* (C)2024 */
package com.codeplanks.home360.controller;

import com.codeplanks.home360.domain.auth.AuthenticationRequest;
import com.codeplanks.home360.domain.auth.AuthenticationResponse;
import com.codeplanks.home360.domain.auth.RegisterRequest;
import com.codeplanks.home360.domain.passwordReset.PasswordResetRequest;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.exception.ApiError;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.service.AuthenticationServiceImpl;
import com.codeplanks.home360.utils.SuccessDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
  private final ApplicationEventPublisher publisher;
  private final RegistrationCompleteEventListener eventListener;

  @Value("${application.frontend.reset-password.url}")
  private String resetPasswordUrl;

  @Value("${application.frontend.verify-email.url}")
  private String emailVerificationUrl;

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
      @RequestBody @Valid RegisterRequest request, final HttpServletRequest servletRequest) {
    SuccessDataResponse<String> newUser = new SuccessDataResponse<>();
    AppUser response = authenticationServiceImpl.register(request);
    newUser.setData("Registration Successful. A verification link have been sent to your email.");
    newUser.setMessage("User registration successful");
    newUser.setStatus(HttpStatus.CREATED);
    publisher.publishEvent(new RegistrationCompleteEvent(response, applicationUrl(servletRequest)));
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
      @RequestParam("token") String oldToken, final HttpServletRequest request)
      throws MessagingException, UnsupportedEncodingException {
    VerificationToken verificationToken =
        authenticationServiceImpl.generateNewVerificationToken(oldToken);
    AppUser appUser = verificationToken.getUser();
    resendVerificationTokenEmail(appUser, emailVerificationUrl, verificationToken);
    SuccessDataResponse<String> response =
        new SuccessDataResponse<>(
            HttpStatus.OK,
            "Success",
            "A new verification link has been sent to your email. Check your inbox to activate "
                + "your account");
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
    response.setData(authenticationServiceImpl.refreshToken(tokenRequest));
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
    Optional<AppUser> user =
        Optional.of(
            authenticationServiceImpl
                .findByEmail(passwordRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User does not exist")));
    String passwordResetToken = UUID.randomUUID().toString();
    authenticationServiceImpl.createPasswordResetTokenForUser(user.get(), passwordResetToken);
    passwordResetEmailLink(user.get(), passwordResetToken);
    SuccessDataResponse<String> response = new SuccessDataResponse<>();
    response.setData("Password reset link has been sent your registered email.");
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
      @RequestBody PasswordResetRequest passwordResetRequest, @RequestParam("token") String token) {
    SuccessDataResponse<String> response =
        new SuccessDataResponse<>(
            HttpStatus.CREATED,
            "Success",
            authenticationServiceImpl.resetPassword(passwordResetRequest, token));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(
      summary = "Change user password",
      description = "Changes the password of users who wants to knowingly change it.",
      tags = {"POST"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
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
  public String changePassword(@RequestBody PasswordResetRequest requestObject) {
    AppUser appUser = authenticationServiceImpl.findByEmail(requestObject.getEmail()).get();
    if (!authenticationServiceImpl.oldPasswordIsValid(appUser, requestObject.getOldPassword())) {
      return "Incorrect old password";
    }
    authenticationServiceImpl.changePassword(appUser, requestObject.getNewPassword());
    return "Password changed successfully";
  }

  private void passwordResetEmailLink(AppUser user, String passwordToken)
      throws MessagingException, UnsupportedEncodingException {
    String url = resetPasswordUrl + "/?token=" + passwordToken;
    eventListener.sendPasswordResetEmail(url);
  }

  private void resendVerificationTokenEmail(
      AppUser user, String applicationUrl, VerificationToken verificationToken)
      throws MessagingException, UnsupportedEncodingException {
    String url = applicationUrl + "?token=" + verificationToken.getToken();
    eventListener.sendVerificationEmail(url);
  }

  public String applicationUrl(HttpServletRequest request) {
    return "http://"
        + request.getServerName()
        + ":"
        + request.getServerPort()
        + "/api/v1/auth"
        + request.getContextPath();
  }
}
