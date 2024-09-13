/* (C)2024 */
package com.codeplanks.home360.config;

import com.codeplanks.home360.converter.*;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {
  @Bean
  public MongoCustomConversions mongoCustomConversions() {
    return new MongoCustomConversions(
        List.of(
            new ArrayListToStringConverter(),
            new StringToListingEnquiryMessageReplyArrayListConverter(),
            new ListingEnquiryMessageReplyArrayListToStringConverter()
            ));
  }
}
