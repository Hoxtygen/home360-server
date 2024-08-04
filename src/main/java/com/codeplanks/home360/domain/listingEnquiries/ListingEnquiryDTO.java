package com.codeplanks.home360.domain.listingEnquiries;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingEnquiryDTO {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String location;
  private String message;
  private String salutation;
  private EmploymentStatus employmentStatus;
  private String pets;
  private String commercialPurpose;
  private String listingId;
  private Integer agentId;
  private LocalDateTime createdAt;
}
