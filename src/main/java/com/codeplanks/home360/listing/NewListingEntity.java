package com.codeplanks.home360.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Document(collection = "newListings")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public abstract class NewListingEntity {
  @Id
  private String id;

  @Field(name = "title")
  @NotBlank(message = "Title is required")
  private String title;

  @Field(name = "description")
  @NotBlank(message = "Description is required")
  private String description;

  @Field(name = "position")
  private String position;

  @Field(name = "miscellaneous")
  private String miscellaneous;

  @Field(name = "address")
  @NotNull(message = "Address is required")
  @Valid
  private Address address;

  @Field(name = "available")
  private boolean available = true;

  @Field(name = "agent_id")
  private Integer agentId;

  @Field(name = "property_images", targetType = FieldType.ARRAY)
  private List<String> propertyImages;

  @CreatedDate
  @Field(name = "created_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Field(name = "updated_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime updatedAt;

  @Field(name = "type")
  @NotNull(message = "Listing type  is required")
  private ListingType type;

}
