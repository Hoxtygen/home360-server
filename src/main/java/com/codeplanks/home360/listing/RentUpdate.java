package com.codeplanks.home360.listing;

import com.codeplanks.home360.validation.IsBoolean;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RentUpdate {
  @NotNull(message = "ListingId field is required")
  @Size(min = 24, max = 24, message = "Enter a valid listingId")
  public String listingId;

  @NotNull(message = "isRented field is required")
  @IsBoolean(message = "field should be a boolean value")
  public Boolean isRented;
}
