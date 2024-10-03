/* (C)2024 */
package com.codeplanks.home360.domain.user;

public class UserMapper {
  public static AppUserDTO mapAppUserToAppUserDTO(AppUser user) {
    return new AppUserDTO(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getAddress(),
        user.getPhoneNumber(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getRole(),
        user.isEnabled());
  }
}
