package com.codeplanks.home360.utils;

import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

public class CustomMongoConversions extends MongoCustomConversions {
  public CustomMongoConversions() {
    super(getConvertersToRegister());
  }

  private static List<Object> getConvertersToRegister() {
    List<Object> converters = new ArrayList<>();
    converters.add(new ArrayListToStringConverter());
    // Add any other custom converters if needed
    return converters;
  }
}
