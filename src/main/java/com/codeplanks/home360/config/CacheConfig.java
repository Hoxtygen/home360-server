/* (C)2025 */
package com.codeplanks.home360.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class CacheConfig {

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);

    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<>(Object.class);
    serializer.setObjectMapper(objectMapper);

    RedisCacheConfiguration cacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer));

    //    Jackson2JsonRedisSerializer<PaginatedListingResponse> paginatedListingSerializer =
    //        new Jackson2JsonRedisSerializer<>(PaginatedListingResponse.class);
    //    paginatedListingSerializer.setObjectMapper(objectMapper);
    //
    //    RedisCacheConfiguration paginatedListingCacheConfiguration =
    //        RedisCacheConfiguration.defaultCacheConfig()
    //            .entryTtl(Duration.ofHours(1))
    //            .serializeValuesWith(
    //                RedisSerializationContext.SerializationPair.fromSerializer(
    //                    paginatedListingSerializer));
    //
    //    Jackson2JsonRedisSerializer<PaginatedListingEnquiriesResponse>
    //        paginatedListingEnquiriesSerializer =
    //            new Jackson2JsonRedisSerializer<>(PaginatedListingEnquiriesResponse.class);
    //    paginatedListingEnquiriesSerializer.setObjectMapper(objectMapper);
    //
    //    RedisCacheConfiguration paginatedListingEnquiriesCacheConfiguration =
    //        RedisCacheConfiguration.defaultCacheConfig()
    //            .entryTtl(Duration.ofHours(1))
    //            .serializeValuesWith(
    //                RedisSerializationContext.SerializationPair.fromSerializer(
    //                    paginatedListingEnquiriesSerializer));

    //    Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
    //    cacheConfigurationMap.put("objectCache", cacheConfiguration);
    //    cacheConfigurationMap.put("paginatedListingsCache", paginatedListingCacheConfiguration);
    //    cacheConfigurationMap.put(
    //        "paginatedListingEnquiriesCache", paginatedListingEnquiriesCacheConfiguration);

    return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfiguration).build();
  }
}
