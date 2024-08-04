package com.codeplanks.home360.domain.listing;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedResponse<T> {
  private int currentPage;
  private long totalItems;
  private int totalPages;
  private List<T> items;
  private boolean hasNext;
}
