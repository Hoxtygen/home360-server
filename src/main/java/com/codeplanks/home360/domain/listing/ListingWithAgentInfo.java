/* (C)2025 */
package com.codeplanks.home360.domain.listing;

import java.io.Serializable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ListingWithAgentInfo implements Serializable {
  private ListingWithViewCountDTO listing;
  private ListingAgentInfo agentInfo;
}
