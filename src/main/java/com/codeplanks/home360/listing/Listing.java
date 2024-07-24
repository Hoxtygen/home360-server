package com.codeplanks.home360.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "listingBuilder")
@Document(collection = "listings")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class Listing {
  @Id
  private String id;

  @Field(name = "title")
  @NotBlank(message = "Title is required")
  private String title;

  @Field(name = "description")
  @NotBlank(message = "Description is required")
  private String description;

  @Field(name = "furnishing")
  private String furnishing;

  @Field(name = "position")
  private String position;

  @Field(name = "miscellaneous")
  private String miscellaneous;

  @Field(name = "address")
  @NotNull(message = "Address is required")
  @Valid
  private Address address;

  @Field(name = "draft")
  @Builder.Default
  private boolean isDraft = false;

  @Field(name = "draftDate", targetType = FieldType.DATE_TIME)
  @Builder.Default
  private LocalDateTime draftDate = null;

  @Field(name = "agent_id")
  private Integer agentId;

  @Field(name = "available_from", targetType = FieldType.DATE_TIME)
  @NotNull(message = "Date apartment will be available is required")
  private LocalDateTime availableFrom;

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

  @Field(name = "apartment_images", targetType = FieldType.ARRAY)
  private List<String> apartmentImages;

  @CreatedDate
  @Field(name = "created_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Field(name = "updated_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime updatedAt;

  @Field(name = "rented", targetType = FieldType.BOOLEAN)
  @Builder.Default
  private  boolean isRented = false;

  @Field(name = "rentDate", targetType = FieldType.DATE_TIME)
  @Builder.Default
  private LocalDateTime rentDate = null;
}
