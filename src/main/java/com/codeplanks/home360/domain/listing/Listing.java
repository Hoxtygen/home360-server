/* (C)2024 */
package com.codeplanks.home360.domain.listing;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "listingBuilder")
@Document(collection = "listings")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class Listing {
  @Id private String id;

  @Field(name = "title")
  private String title;

  @Field(name = "description")
  private String description;

  @Field(name = "furnishing")
  private String furnishing;

  @Field(name = "position")
  private String position;

  @Field(name = "miscellaneous")
  private String miscellaneous;

  @Field(name = "address")
  private Address address;

  @Field(name = "agent_id")
  private Integer agentId;

  @Field(name = "available_from", targetType = FieldType.DATE_TIME)
  private LocalDateTime availableFrom;

  @Field(name = "cost")
  private ListingCost cost;

  @Field(name = "details", targetType = FieldType.ARRAY)
  private List<String> details;

  @Field(name = "facility_quality")
  private FacilityQuality facilityQuality;

  @Field(name = "pets_allowed")
  private PetsAllowed petsAllowed;

  @Field(name = "apartment_info")
  private ApartmentInfo apartmentInfo;

  @Field(name = "application_docs", targetType = FieldType.ARRAY)
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
  private boolean rented = false;

  @Field(name = "rentDate", targetType = FieldType.DATE_TIME)
  @Builder.Default
  private LocalDateTime rentDate = null;
}
