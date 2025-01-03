/* (C)2025 */
package com.codeplanks.home360.domain.listing;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ListingWithAgentInfo {
  private ListingWithViewCountDTO listing;
  private ListingAgentInfo agentInfo;
}
