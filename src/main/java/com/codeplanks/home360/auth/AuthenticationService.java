package com.codeplanks.home360.auth;


import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.exception.UserExistsException;
import com.codeplanks.home360.exception.UserNotFoundException;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.user.Role;
import com.codeplanks.home360.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(@RequestBody RegisterRequest request) {
        Optional<AppUser> newUserEmail = userRepository.findByEmail(request.getEmail());
        Optional<AppUser> newUserPhoneNumber =
                userRepository.findByPhoneNumber(request.getPhoneNumber());
        if (newUserEmail.isPresent()) {
            throw new UserExistsException("User with email " + request.getEmail() + " " +
                    "already exists");
        }

        if (newUserPhoneNumber.isPresent()) {
            throw new UserExistsException("User with phone number " + request.getPhoneNumber()
                    + " already exists"
            );
        }
        System.out.println("Inside auth service");
        AppUser user = AppUser.builder()
                              .firstName(request.getFirstName())
                              .lastName(request.getLastName())
                              .email(request.getEmail())
                              .address(request.getAddress())
                              .phoneNumber(request.getPhoneNumber())
                              .password(passwordEncoder.encode(request.getPassword()))
                              .role(Role.USER)
                              .build();

     AppUser newUser =  userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getUsername())
                .message("User signup successful")
                .build();
    }

    public ResponseEntity<?> login(AuthenticationRequest request) throws Exception {
        Optional<AppUser> newUserEmail = userRepository.findByEmail(request.getEmail());
        if (newUserEmail.isEmpty()) {
            throw new UserNotFoundException("User with email " + request.getEmail() +
                    " does not exist");
        }
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request
                            .getEmail(), request.getPassword()));
            if (authentication.isAuthenticated()) {
                var user = userRepository.findByEmail(request.getEmail())
                                         .orElseThrow(() -> new UserNotFoundException(
                                                 "User does not exist."));
                var jwtToken = jwtService.generateToken(user);
                responseMap.put("message", "login successful");
                responseMap.put("firstName", user.getFirstName());
                responseMap.put("lastName", user.getLastName());
                responseMap.put("token", jwtToken);
                responseMap.put("email", user.getEmail());
                return ResponseEntity.ok(responseMap);
//                return AuthenticationResponse
//                        .builder()
//                        .token(jwtToken)
//                        .firstName(user.getFirstName())
//                        .lastName(user.getLastName())
//                        .email(user.getEmail())
//                        .build();
            } else {
                responseMap.put("error", true);
                responseMap.put("message", "Invalid Credentials");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (BadCredentialsException badCredentials) {
            responseMap.put("message", "Username/password not correct");
            responseMap.put("error", true);
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("error", true);
            responseMap.put("message", "Something went wrong");
            return ResponseEntity.status(500).body(responseMap);
        }

    }

}
