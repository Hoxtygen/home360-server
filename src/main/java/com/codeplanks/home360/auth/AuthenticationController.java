package com.codeplanks.home360.auth;


import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.token.TokenRequest;
import com.codeplanks.home360.token.TokenResponse;
import com.codeplanks.home360.token.passwordReset.PasswordResetRequest;
import com.codeplanks.home360.token.verificationToken.VerificationToken;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.utils.SuccessDataResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;


/**
 * @author Wasiu Idowu
 */
@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationServiceImpl authenticationServiceImpl;
  private final ApplicationEventPublisher publisher;
  private final RegistrationCompleteEventListener eventListener;
  @Value("${application.frontend.reset-password.url}")
  private String resetPasswordUrl;
  @Value("${application.frontend.verify-email.url}")
  private String emailVerificationUrl;

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

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
          @RequestBody AuthenticationRequest request) {
    return new ResponseEntity<>(authenticationServiceImpl.login(request), HttpStatus.OK);
  }

  @GetMapping("/verifyEmail")
  public ResponseEntity<SuccessDataResponse<String>> verifyEmail(
          @RequestParam("token") String token) {
    SuccessDataResponse<String> response = new SuccessDataResponse<>(
            HttpStatus.OK,
            "Success",
            authenticationServiceImpl.verify(token));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/resend-verification-token")
  public ResponseEntity<SuccessDataResponse<String>> resendVerificationToken(
          @RequestParam("token") String oldToken,
          final HttpServletRequest request)
          throws MessagingException, UnsupportedEncodingException {
    VerificationToken verificationToken =
            authenticationServiceImpl.generateNewVerificationToken(oldToken);
    AppUser appUser = verificationToken.getUser();
    resendVerificationTokenEmail(appUser, emailVerificationUrl, verificationToken);
    SuccessDataResponse<String> response = new SuccessDataResponse<>(HttpStatus.OK, "Success",
            "A new verification link has been sent to your email. Check your inbox to activate " +
                    "your account");
    return new ResponseEntity<>(response, HttpStatus.OK);

  }

  @PostMapping("/refreshToken")
  public ResponseEntity<SuccessDataResponse<TokenResponse>> getRefreshToken(
          @RequestBody TokenRequest tokenRequest) {
    SuccessDataResponse<TokenResponse> response = new SuccessDataResponse<>();
    response.setData(authenticationServiceImpl.refreshToken(tokenRequest));
    response.setMessage("Success");
    response.setStatus(HttpStatus.CREATED);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/password-reset-request")
  public ResponseEntity<SuccessDataResponse<String>> resetPasswordRequest(
          @RequestBody PasswordResetRequest passwordRequest)
          throws MessagingException, UnsupportedEncodingException {
    Optional<AppUser> user =
            Optional.of(authenticationServiceImpl.findByEmail(passwordRequest.getEmail())
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

  @PostMapping("/reset-password")
  public ResponseEntity<SuccessDataResponse<String>> resetPassword(
          @RequestBody PasswordResetRequest passwordResetRequest,
          @RequestParam("token") String token) {
    SuccessDataResponse<String> response = new SuccessDataResponse<>(HttpStatus.OK, "Success",
            authenticationServiceImpl.resetPassword(passwordResetRequest, token));
    return new ResponseEntity<>(response, HttpStatus.OK);

  }

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

  private void resendVerificationTokenEmail(AppUser user, String applicationUrl,
                                            VerificationToken verificationToken)
          throws MessagingException, UnsupportedEncodingException {
    String url = applicationUrl + "?token=" + verificationToken.getToken();
    eventListener.sendVerificationEmail(url);
  }

  public String applicationUrl(HttpServletRequest request) {
    return "http://" + request.getServerName() + ":" + request.getServerPort()
            + "/api/v1/auth" + request.getContextPath();
  }

}
