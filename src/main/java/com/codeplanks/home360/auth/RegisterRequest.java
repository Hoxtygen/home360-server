package com.codeplanks.home360.auth;


import com.codeplanks.home360.user.Role;
import com.codeplanks.home360.validation.Password;
import com.codeplanks.home360.validation.ValidEmail;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotNull(message = "First name is required")
  @Size(min = 3, max = 50, message = "First name should be between 3-50 characters")
  private String firstName;

  @NotNull(message = "Last name is required")
  @Size(min = 3, max = 50, message = "Last name should be between 3-50 characters")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Enter a valid email address")
  @ValidEmail(message = "Enter a valid email address")
  private String email;

  @Password(message = "Password is required")
  private String password;

  @Size(min = 11, max = 11, message = "Phone number must be 11 characters long.")
  @NotNull(message = "Phone number is required")
  @Pattern(regexp = "^([0]{1})([7-9]{1})([0|1]{1})([\\d]{1})([\\d]{7,8})$", message =
          "Enter a valid Nigerian number E.g 08022345678")
  private String phoneNumber;

  @NotNull(message = "Address is required")
  @Size(min = 20, message = "Address must be have minimum of 20 characters")
  private String address;

}
