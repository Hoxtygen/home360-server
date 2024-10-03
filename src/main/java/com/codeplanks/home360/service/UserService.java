/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.PasswordChangeRequest;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.AppUserDTO;
import java.util.Optional;

public interface UserService {
  String changeUserPassword(PasswordChangeRequest request);

  Integer extractUserId();

  boolean isOldPasswordValid(AppUser appUser, String oldPassword);

  AppUser getUserByUserId(Integer userId);

  void updatePassword(AppUser appUser, String newPassword);

  Optional<AppUser> findByEmail(String email);

  boolean emailExists(String email);

  boolean phoneNumberExists(String phoneNumber);

  AppUserDTO getUserDetails();
}
