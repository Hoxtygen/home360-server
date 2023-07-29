package com.codeplanks.home360.listing;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "listings")
public class ListingRequest {
  //  @NotNull(message = "Name is required")
//  @Size(min = 5, message = "name should be at least 5 characters long")
//  private String name;
//
//  @NotNull(message = "Address is required")
//  @Size(min = 5, message = "address should be at least 5 characters long")
//  private String address;
//
//  @NotNull(message = "Indicate if this listing is still available")
//  private Boolean available;
//
//  @NotNull(message = "City is required")
//  private String city;
//
//  @NotNull(message = "City is required")
//  private String state;
//
//  @NotNull(message = "description is required")
//  private String description;
//
//  @NotNull(message = "Image is required")
//  private String images;
  @Id
  private String id;

  @NotNull(message = "Agent id is required")
  private Integer agentId;

  @NotNull(message = "You must add listing requirements")
  private  Listing listing;

}
