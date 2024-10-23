/* (C)2024 */
package com.codeplanks.home360.domain.listingEnquiries;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingEnquiryDTO {

  @NotBlank(message = "First name is required")
  @Size(min = 3, max = 50, message = "First name should be between 3-50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 3, max = 50, message = "Last name should be between 3-50 characters")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Enter a valid email address")
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(
      regexp = "^([0]{1})([7-9]{1})([0|1]{1})([\\d]{1})([\\d]{7,8})$",
      message = "Enter a valid Nigerian number E.g 08022345678")
  @Size(min = 11, max = 11, message = "Phone number must be 11 characters long.")
  private String phoneNumber;

  @NotBlank(message = "Location is required")
  @Size(min = 3, max = 50, message = "Location should be between 3-50 characters")
  private String location;

  @NotBlank(message = "Message is required")
  @Size(min = 3, max = 2000, message = "message should be between 3-2000 characters")
  private String message;

  @NotBlank(message = "Salutation is required")
  private String salutation;

  @NotNull(message = "Employment status is required")
  @Valid
  private EmploymentStatus employmentStatus;

  @NotBlank(message = "Pets is required")
  private String pets;

  @NotBlank(message = "Commercial purpose is required")
  private String commercialPurpose;

  @NotBlank(message = "ListingId field is required")
  private String listingId;

  @NotNull(message = "Agent Id is required")
  private Integer agentId;

  private Integer userId;

  private LocalDateTime createdAt;
}
