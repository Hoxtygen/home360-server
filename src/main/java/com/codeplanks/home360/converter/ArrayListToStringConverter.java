/* (C)2024 */
package com.codeplanks.home360.converter;

import java.util.ArrayList;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ArrayListToStringConverter implements Converter<ArrayList<String>, String> {
  @Override
  public String convert(@NonNull ArrayList<String> source) {
    return String.join(", ", source);
  }
}
