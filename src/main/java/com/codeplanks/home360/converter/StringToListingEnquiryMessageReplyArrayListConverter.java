/* (C)2024 */
package com.codeplanks.home360.converter;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReply;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@ReadingConverter
public class StringToListingEnquiryMessageReplyArrayListConverter
    implements Converter<String, ArrayList<ListingEnquiryMessageReply>> {
  private final ObjectMapper objectMapper;

  public StringToListingEnquiryMessageReplyArrayListConverter() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public ArrayList<ListingEnquiryMessageReply> convert(@NonNull String source) {

    try {
      System.out.println("Converting Json string to List " + source);
      return objectMapper.readValue(
          source,
          objectMapper
              .getTypeFactory()
              .constructCollectionType(List.class, ListingEnquiryMessageReply.class));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to convert JSON string to List", e);
    }
  }
}
