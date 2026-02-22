/* (C)2025 */
package com.codeplanks.home360.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Date;

@WritingConverter
public class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
  @Override
  public Date convert(@NonNull ZonedDateTime source) {
    return Date.from(source.toInstant());
  }
}
