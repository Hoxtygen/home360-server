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
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

  @Bean
  public RedisTemplate<String, Object> sessionIdRedisTemplate(
      RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Use String serializers for keys and hash keys (human-readable format)
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());

    template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));

    template.setValueSerializer(new GenericToStringSerializer<>(Object.class));

    return template;
  }
}
