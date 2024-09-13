/* (C)2024 */
package com.codeplanks.home360.converter;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReply;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

@WritingConverter
public class ListingEnquiryMessageReplyArrayListToStringConverter implements Converter<ArrayList<ListingEnquiryMessageReply>, String> {
  private final ObjectMapper objectMapper;

  public ListingEnquiryMessageReplyArrayListToStringConverter() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public String convert(@NonNull ArrayList<ListingEnquiryMessageReply> source) {
    try {
      return objectMapper.writeValueAsString(source);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to convert List to JSON string", e);
    }
  }

}
