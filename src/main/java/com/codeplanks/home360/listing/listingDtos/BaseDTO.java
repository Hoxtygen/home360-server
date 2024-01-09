package com.codeplanks.home360.listing.listingDtos;


import com.codeplanks.home360.listing.Address;
import com.codeplanks.home360.listing.ListingType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class BaseDTO {
  private String id;
  private String title;
  private String description;
  private String position;
  private String miscellaneous;
  private Address address;
  private boolean available;
  private Integer agentId;
  private List<String> propertyImages;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private ListingType type;
}
