package com.codeplanks.home360.auth;


import com.codeplanks.home360.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
          @RequestBody @Valid RegisterRequest request) {
    return new ResponseEntity<>(authenticationService.register(request),
            HttpStatus.CREATED);

  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
          @RequestBody AuthenticationRequest request) {
    return new ResponseEntity<>(authenticationService.login(request), HttpStatus.OK);
  }

}
