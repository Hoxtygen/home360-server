package com.codeplanks.home360.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class HomeProperties {
  @Field(name = "furnishing")
  private String furnishing;

  @Field(name = "available")
  @Builder.Default
  private boolean available = true;

  @Field(name = "available_from", targetType = FieldType.DATE_TIME)
  @NotNull(message = "Date apartment will be available is required")
  private Date availableFrom;

  @Field(name = "cost")
  @NotNull(message = "Cost is required")
  @Valid
  private ListingCost cost;

  @Field(name = "details", targetType = FieldType.ARRAY)
  @NotNull(message = "Apartment details is required")
  private List<String> details;

  @Field(name = "facility_quality")
  @Valid
  @NotNull(message = "Facility quality is required")
  private FacilityQuality facilityQuality;

  @Field(name = "pets_allowed")
  @NotNull(message = "Indicate if pet is allowed")
  private PetsAllowed petsAllowed;

  @Field(name = "apartment_info")
  @NotNull(message = "Apartment information is required")
  private ApartmentInfo apartmentInfo;

  @Field(name = "application_docs", targetType = FieldType.ARRAY)
  @NotNull
  private List<String> applicationDocs;

}
