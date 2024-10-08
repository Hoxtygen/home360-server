/* (C)2024 */
package com.codeplanks.home360.domain.listing;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingDTO {

  @NotBlank(message = "Title is required")
  private String title;

  private Integer agentId;

  @NotBlank(message = "Description is required")
  private String description;

  private String furnishing;

  private String position;

  private String miscellaneous;

  @NotNull(message = "Address is required")
  @Valid
  private Address address;

  @NotNull(message = "Date apartment will be available is required")
  private LocalDateTime availableFrom;

  @NotNull(message = "Cost is required")
  @Valid
  private ListingCost cost;

  @NotNull(message = "Apartment details is required")
  private List<String> details;

  @Valid
  @NotNull(message = "Facility quality is required")
  private FacilityQuality facilityQuality;

  @NotNull(message = "Indicate if pet is allowed")
  @Valid
  private PetsAllowed petsAllowed;

  @NotNull(message = "Apartment information is required")
  @Valid
  private ApartmentInfo apartmentInfo;

  @NotNull private List<String> applicationDocs;

  private List<String> apartmentImages;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
