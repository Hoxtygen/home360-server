package com.codeplanks.home360.auth;


import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.utils.SuccessDataResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author Wasiu Idowu
 */
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationServiceImpl authenticationServiceImpl;
  private final ApplicationEventPublisher publisher;
  Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @PostMapping("/register")
  public ResponseEntity<SuccessDataResponse<String>> register(
          @RequestBody @Valid RegisterRequest request, final HttpServletRequest servletRequest) {
    SuccessDataResponse<String> newUser = new SuccessDataResponse<>();
    AppUser response = authenticationServiceImpl.register(request);
    newUser.setData("Registration Successful");
    newUser.setMessage("User registration successful");
    newUser.setStatus(HttpStatus.CREATED);
    publisher.publishEvent(new RegistrationCompleteEvent(response, applicationUrl(servletRequest)));
    return new ResponseEntity<>(newUser,
            HttpStatus.CREATED);

  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
          @RequestBody AuthenticationRequest request) {
    return new ResponseEntity<>(authenticationServiceImpl.login(request), HttpStatus.OK);
  }

  @GetMapping("/verifyEmail")
  public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
    return new ResponseEntity<String>(authenticationServiceImpl.verify(token), HttpStatus.OK);
  }

  public String applicationUrl(HttpServletRequest request) {
    return "http://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/auth" + request.getContextPath();
  }

}
