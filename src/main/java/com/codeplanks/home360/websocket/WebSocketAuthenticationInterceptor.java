/* (C)2025 */
package com.codeplanks.home360.websocket;

import com.codeplanks.home360.config.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private static final Logger logger =
      LoggerFactory.getLogger(WebSocketAuthenticationInterceptor.class);

  public WebSocketAuthenticationInterceptor(
      JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
      if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        try {
          String username = jwtService.extractUsername(token);
          if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
              UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(
                      userDetails, null, userDetails.getAuthorities());

              // Store authentication in the session
              accessor.getSessionAttributes().put("SPRING_SECURITY_CONTEXT", authentication);
              accessor.setUser(authentication);
              logger.info("User {} authenticated successfully via WebSocket", username);
            } else {
              logger.warn("Invalid JWT token for user: {}", username);
            }
          }
        } catch (UsernameNotFoundException e) {
          logger.error("User not found: {}", e.getMessage());
        } catch (Exception e) {
          logger.error("Authentication error: {}", e.getMessage(), e);
        }
      } else {
        logger.warn("No or invalid Authorization header found in WebSocket connection");
      }
    }
    return message;
  }
}
