/* (C)2025 */
package com.codeplanks.home360.domain.rental;

import com.codeplanks.home360.validation.ValidEmail;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
  @NotNull(message = "Rent start date is required")
  private LocalDateTime rentStartDate;

  @NotNull(message = "Rent due date is required")
  private LocalDateTime rentDueDate;

  @NotNull(message = "listing ID is required")
  private String listingId;

  @NotNull(message = "Renter email is required")
  @ValidEmail
  private String renterEmail;

  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
