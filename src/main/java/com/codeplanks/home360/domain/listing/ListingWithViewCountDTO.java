/* (C)2025 */
package com.codeplanks.home360.domain.listing;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingWithViewCountDTO {
  private String id;
  private String title;
  private String description;
  private String furnishing;
  private String position;
  private String miscellaneous;
  private Address address;
  private Integer agent_id;
  private LocalDateTime available_from;
  private ListingCost cost;
  private List<String> details;
  private FacilityQuality facility_quality;
  private PetsAllowed pets_allowed;
  private ApartmentInfo apartment_info;
  private List<String> application_docs;
  private List<String> apartment_images;
  private int viewCount;
  private LocalDateTime created_at;
  private LocalDateTime updated_at;
  private boolean rented;
  private LocalDateTime rentDate;
}
