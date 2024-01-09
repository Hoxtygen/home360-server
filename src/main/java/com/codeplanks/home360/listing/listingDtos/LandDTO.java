package com.codeplanks.home360.listing.listingDtos;


import com.codeplanks.home360.listing.Address;
import com.codeplanks.home360.listing.LandProperties;
import com.codeplanks.home360.listing.ListingType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class LandDTO extends BaseDTO {
  private LandProperties properties;

  public LandDTO(
          String id, String title, String description, String position,
          String miscellaneous, Address address, boolean available,
          Integer agentId, List<String> propertyImages, LocalDateTime createdAt,
          LocalDateTime updatedAt, ListingType type, LandProperties properties
  ) {
    super(
            id, title, description,
            position, miscellaneous, address,
            available, agentId, propertyImages,
            createdAt, updatedAt, type
    );
    this.properties = properties;
  }

}
