package com.codeplanks.home360.auth;


import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.user.IUserService;
import com.codeplanks.home360.user.Role;
import com.codeplanks.home360.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IUserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) throws UserAlreadyExistsException {
        boolean newUserEmail = emailExists(request.getEmail());
        boolean newUserPhoneNumber = phoneNumberExists(request.getPhoneNumber());
        if (newUserEmail) {
            throw new UserAlreadyExistsException("User with email " + request
                    .getEmail() + " " +
                    "already exists");
        }

        if (newUserPhoneNumber) {
            throw new UserAlreadyExistsException("User with phone number " + request
                    .getPhoneNumber()
                    + " already exists"
            );
        }

        AppUser user = AppUser.builder()
                              .firstName(request.getFirstName())
                              .lastName(request.getLastName())
                              .email(request.getEmail())
                              .address(request.getAddress())
                              .phoneNumber(request.getPhoneNumber())
                              .password(passwordEncoder.encode(request.getPassword()))
                              .role(Role.USER)
                              .createdAt(new Date())
                              .updatedAt(new Date())
                              .build();

        AppUser newUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getUsername())
                .message("User signup successful")
                .build();
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
}
