package com.codeplanks.home360.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingDTO {

  private String id;
  private String title;
  private Integer agentId;
  private String description;
  private String furnishing;
  private String position;
  private String miscellaneous;
  private Address address;
  private boolean isDraft;
  private boolean isRented;
  private Date availableFrom;
  private ListingCost cost;
  private List<String> details;
  private FacilityQuality facilityQuality;
  private PetsAllowed petsAllowed;
  private ApartmentInfo apartmentInfo;
  private List<String> applicationDocs;
  private List<String> apartmentImages;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime draftDate;
  private LocalDateTime rentDate;

}


