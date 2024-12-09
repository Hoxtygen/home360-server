/* (C)2024 */
package com.codeplanks.home360.domain.listingView;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
