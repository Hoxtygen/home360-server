package com.codeplanks.home360.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "newListings")
@Getter
@Setter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class Home extends NewListingEntity {
  @NotNull(message = "Building information is required")
  @Valid
  private HomeProperties properties;
}
