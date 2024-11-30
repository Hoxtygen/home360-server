package com.codeplanks.home360.domain.listing;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MonthlyListings {
  private int year;
  private Map<String, Integer> months;

  @Override
  public String toString() {
    return "MonthlyListings{" +
            "year=" + year +
            ", months=" + months +
            '}';
  }
}
