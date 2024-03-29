package com.codeplanks.home360.config;

import com.codeplanks.home360.utils.ArrayListToStringConverter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
//@EnableAutoConfiguration(exclude = {AutoConf})
public class MongoConfig {
  @Bean
  public MongoCustomConversions mongoCustomConversions() {
    return new MongoCustomConversions(List.of(new ArrayListToStringConverter()));
  }
}
