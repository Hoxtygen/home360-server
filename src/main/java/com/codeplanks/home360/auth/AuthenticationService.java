package com.codeplanks.home360.auth;

public interface AuthenticationService {
  AuthenticationResponse register(RegisterRequest request);
  AuthenticationResponse login(AuthenticationRequest request);
}
