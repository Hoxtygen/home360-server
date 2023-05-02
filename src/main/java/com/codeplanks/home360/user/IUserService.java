package com.codeplanks.home360.user;

import com.codeplanks.home360.auth.AuthenticationRequest;
import com.codeplanks.home360.auth.AuthenticationResponse;
import com.codeplanks.home360.auth.RegisterRequest;

public interface IUserService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(AuthenticationRequest request);
}
