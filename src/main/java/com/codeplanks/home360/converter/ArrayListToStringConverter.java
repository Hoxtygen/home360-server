/* (C)2024 */
package com.codeplanks.home360.converter;

import java.util.ArrayList;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@ReadingConverter
public class ArrayListToStringConverter implements Converter<ArrayList<String>, String> {
  @Override
  public String convert(@NonNull ArrayList<String> source) {
    return String.join(", ", source);
  }
}
