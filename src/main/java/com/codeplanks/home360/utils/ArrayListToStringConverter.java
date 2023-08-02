package com.codeplanks.home360.utils;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.ArrayList;

@ReadingConverter
public class ArrayListToStringConverter implements Converter<ArrayList<String>, String> {
  @Override
  public String convert(@NonNull ArrayList<String> source) {
    return String.join(", ", source);
  }

}


