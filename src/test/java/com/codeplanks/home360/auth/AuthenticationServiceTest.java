package com.codeplanks.home360.auth;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.user.Role;
import com.codeplanks.home360.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @InjectMocks
  private AuthenticationService authenticationServiceTest;

  @Mock
  private UserRepository userRepositoryTest;

  @Mock
  private JwtService jwtService;
  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  private AuthenticationService authenticationService;


  final String token = "eyJhbGciOiJIUzUxMiJ9" +
          ".eyJzdWIiOiJtaW1vc2FAZXhhbXBsZS5jb20iLCJpYXQiOjE2OTExNjcxNjEsImV4cCI6MTY5MTE2ODYwMX0" +
          ".J8NrbPaIMw8VZWUz5uxZ_aGGTPJnNnn3bn_h0aNXiKGcKQnbMXDha5XpSvlA2WRVzU55jNf_qx9wyc5xH3z7BQ";

  @Test
  void register() {
    AppUser appUserResponse = new AppUser(1, "Shaolin", "Dragon",
            "shaolin@example", passwordEncoder.encode(
            "Int3rnat!onalization"),
            "Ketu Alapere", "09087812347", new Date(),
            new Date(), Role.USER
    );

    final RegisterRequest userToSave = new RegisterRequest("Shaolin", "Dragon", "shaolin@example" +
            ".com", "Int3rnat!onalization", "09087812347", "Ketu Alapere");

    when(userRepositoryTest.save(any(AppUser.class))).thenReturn(appUserResponse);
    when(jwtService.generateToken(appUserResponse)).thenReturn(token);

    final var actual = authenticationServiceTest.register(userToSave);
    Assertions.assertThat(actual.getFirstName()).isEqualTo("Shaolin");
    Assertions.assertThat(actual.getFirstName()).isNotEqualTo("Malaysia");
    Assertions.assertThat(actual.getLastName()).isNotEmpty();

    Assertions.assertThat(actual.getLastName()).isEqualTo("Dragon");
    Assertions.assertThat(actual.getLastName()).isNotEqualTo("Flatulence");
    Assertions.assertThat(actual.getLastName()).isNotEmpty();

    Assertions.assertThat(actual.getEmail()).isEqualTo("shaolin@example");
    Assertions.assertThat(actual.getEmail()).isNotEqualTo("shaolin_temple@example");
    Assertions.assertThat(actual.getEmail()).isNotEmpty();


    Assertions.assertThat(actual.getMessage()).isEqualTo("User signup successful");
    Assertions.assertThat(actual.getMessage()).isNotEqualTo("User signup successfully");
    Assertions.assertThat(actual.getMessage()).isNotEmpty();

    Assertions.assertThat(actual.getToken()).isNotEmpty();
    Assertions.assertThat(actual.getToken()).startsWith("ey");
    Assertions.assertThat(actual.getToken()).doesNotContainOnlyWhitespaces();


  }

  @Test
  @WithMockUser(username = "shaolin@example.com", password = "Int3rnat!onalization", roles = "USER")
  void login() {
    // Given
    final AuthenticationRequest request = new AuthenticationRequest("shaolin@example.com",
            "Int3rnat!onalization");

    AppUser appUserResponse = new AppUser(1, "Shaolin", "Dragon",
            "shaolin@example", passwordEncoder.encode(
            "Int3rnat!onalization"),
            "Ketu Alapere", "09087812347", new Date(),
            new Date(), Role.USER
    );
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()));

    when(userRepositoryTest.findByEmail(request.getEmail()).orElseThrow()).thenReturn(
            appUserResponse);
    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(),
                    request.getPassword()))).thenReturn(authentication);
    when(jwtService.generateToken(appUserResponse)).thenReturn(token);

    AuthenticationResponse response = authenticationServiceTest.login(request);
    Assertions.assertThat(response.getMessage()).isEqualTo("User login successful");
//    final AuthenticationRequest request = new AuthenticationRequest("shaolin@example.com",
//            "Int3rnat!onalization");
//    Authentication authentication = authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(
//                    request.getEmail(),
//                    request.getPassword()
//          User login successful  ));
//
//    when(authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(request.getEmail(),
//                    request.getPassword()))).thenReturn(authentication);
//    when(jwtService.generateToken(appUserResponse)).thenReturn(token);
//    final var actual = authenticationServiceTest.login(request);
//
//    Assertions.assertThat(actual.getMessage()).isEqualTo("Incorrect email/password");

  }
}