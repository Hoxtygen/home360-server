package com.codeplanks.home360.domain.listing;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyIncome {
  private int year;
  private Map<String, Integer> months;

  @Override
  public String toString() {
    return "MonthlyIncome{" + "year=" + year + ", months=" + months + '}';
  }
}
