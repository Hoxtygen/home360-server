/* (C)2025 */
package com.codeplanks.home360.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@ReadingConverter
public class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
  @Override
  public ZonedDateTime convert(@NonNull Date source) {
    return source.toInstant().atZone(ZoneOffset.UTC);
  }
}
