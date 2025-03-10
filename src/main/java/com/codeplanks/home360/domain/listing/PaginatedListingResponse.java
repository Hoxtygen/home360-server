/* (C)2025 */
package com.codeplanks.home360.domain.listing;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedListingResponse {
  private int currentPage;
  private long totalItems;
  private int totalPages;
  private List<Listing> items;
  private boolean hasNext;
}
