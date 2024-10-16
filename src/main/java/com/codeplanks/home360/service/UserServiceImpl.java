/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.PasswordChangeRequest;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.AppUserDTO;
import com.codeplanks.home360.domain.user.UserMapper;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  @Override
  public String changeUserPassword(PasswordChangeRequest request) {
    Integer userId = extractUserId();
    AppUser appUser = getUserByUserId(userId);
    if (!isOldPasswordValid(appUser, request.getOldPassword())) {
      return "Password change failed. Incorrect password";
    }
    updatePassword(appUser, request.getNewPassword());
    return "Password changed successfully";
  }

  @Override
  public AppUserDTO getUserDetails() {
    Integer userId = extractUserId();
    AppUser user = getUserByUserId(userId);
    return UserMapper.mapAppUserToAppUserDTO(user);
  }

  @Override
  public Integer extractUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnAuthorizedException("User is not authenticated");
    }
    String usernames = authentication.getName();
    return getUser(usernames).getId();
  }

  @Override
  public boolean isOldPasswordValid(AppUser appUser, String oldPassword) {
    return passwordEncoder.matches(oldPassword, appUser.getPassword());
  }

  @Override
  public AppUser getUserByUserId(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User does not exist"));
  }

  public AppUser getUser(String email) throws NotFoundException {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User does not exist"));
  }

  @Override
  public void updatePassword(AppUser appUser, String newPassword) {
    appUser.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(appUser);
  }

  @Override
  public AppUser findUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User does " + "not exist"));
  }

  @Override
  public boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  @Override
  public boolean phoneNumberExists(String phoneNumber) {
    return userRepository.findByPhoneNumber(phoneNumber).isPresent();
  }
}
