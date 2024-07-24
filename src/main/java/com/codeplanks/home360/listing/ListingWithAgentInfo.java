package com.codeplanks.home360.listing;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ListingWithAgentInfo {
  private Listing listing;
  private ListingAgentInfo agentInfo;
}
