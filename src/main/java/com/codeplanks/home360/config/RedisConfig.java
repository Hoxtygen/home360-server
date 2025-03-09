/* (C)2025 */
package com.codeplanks.home360.config;

import com.codeplanks.home360.domain.listing.ListingWithAgentInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
  @Bean
  public RedisTemplate<String, ListingWithAgentInfo> redisTemplate(
      RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, ListingWithAgentInfo> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    Jackson2JsonRedisSerializer<ListingWithAgentInfo> serializer =
        new Jackson2JsonRedisSerializer<>(ListingWithAgentInfo.class);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    serializer.setObjectMapper(objectMapper);

    template.setValueSerializer(serializer);
    template.setKeySerializer(template.getStringSerializer());
    template.setHashKeySerializer(template.getStringSerializer());
    template.setHashValueSerializer(serializer);

    return template;
  }
}
