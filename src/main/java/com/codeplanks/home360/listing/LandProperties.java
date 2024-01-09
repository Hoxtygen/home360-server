package com.codeplanks.home360.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class LandProperties {
  @Field(name = "land_area")
  @NotNull(message = "Land area is required")
  private String landArea;

  @Field(name = "price")
  @NotNull(message = "Price is required")
  private String price;

  @Field(name = "development_level")
  @NotNull(message = "Development level information is required")
  @Valid
  private Development developmentLevel;

}