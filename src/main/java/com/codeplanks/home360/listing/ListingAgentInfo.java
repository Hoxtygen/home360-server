package com.codeplanks.home360.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingAgentInfo {
  private String firstName;
  private  String lastName;
  private String email;
  private String phoneNumber;
}
