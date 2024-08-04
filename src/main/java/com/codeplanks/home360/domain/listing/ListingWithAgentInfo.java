package com.codeplanks.home360.domain.listing;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ListingWithAgentInfo {
  private Listing listing;
  private ListingAgentInfo agentInfo;
}
