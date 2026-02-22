package com.codeplanks.home360.domain.listingEnquiries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedListingEnquiriesChat {
  private int currentPage;
  private long totalItems;
  private int totalPages;
  private boolean hasNext;
  private List<EnquiryMessage> items;
}
