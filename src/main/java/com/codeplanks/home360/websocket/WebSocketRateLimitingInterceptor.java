/* (C)2025-2026 */
package com.codeplanks.home360.websocket;

import com.codeplanks.home360.domain.user.AppUser;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class WebSocketRateLimitingInterceptor implements ChannelInterceptor {
  private static final Logger logger =
      LoggerFactory.getLogger(WebSocketRateLimitingInterceptor.class);

  // Expire after 10 minutes of inactivity and cap at 10,000 entries to prevent memory leaks
  private final Cache<String, Bucket> bucketCache =
      Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).maximumSize(10000).build();

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null || accessor.getCommand() == null) {
      return message;
    }

    if (StompCommand.SEND.equals(accessor.getCommand())
        || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      Authentication authentication = (Authentication) accessor.getUser();
      if (authentication != null && authentication.getPrincipal() instanceof AppUser user) {
        String userId = user.getId().toString();
        Bucket bucket = bucketCache.get(userId, k -> createNewBucket());

        if (bucket != null && !bucket.tryConsume(1)) {
          logger.warn("WebSocket rate limit exceeded for user: {}", user.getEmail());
          throw new MessagingException("Too many messages. Please slow down.");
        }
      }
    }

    return message;
  }

  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(
            limit ->
                limit.capacity(100).refillGreedy(100, Duration.ofMinutes(1)).initialTokens(100))
        .build();
  }
}
