package com.codeplanks.home360.domain.listingView;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ListingViewDTO {
  private String id;

  @NotBlank(message = "Listing ID is required")
  private String listingId;

  @NotNull(message = "View timestamp is required")
  private LocalDateTime timestamp;

  private LocalDateTime createdAt;
}
