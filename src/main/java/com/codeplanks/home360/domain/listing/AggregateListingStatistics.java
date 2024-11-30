package com.codeplanks.home360.domain.listing;

import lombok.Data;

import java.util.List;

@Data
public class AggregateListingStatistics {
  private Integer total_listings;
  private Integer rented_listings;
  private Integer total_income;
  private List<MonthlyIncome> income;
  private List<MonthlyListings> listings;
}
