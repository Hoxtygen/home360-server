/* (C)2025 */
package com.codeplanks.home360.websocket;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.utils.AppConstants;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null || accessor.getCommand() == null) {
      return message;
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      return handleConnect(accessor, message);
    }
    return message;
  }

  private Authentication authenticate(String token) {
    String username = jwtService.extractUsername(token);

    if (!StringUtils.hasText(username)) {
      throw new BadCredentialsException("JWT does not contain a valid username");
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (!jwtService.isTokenValid(token, userDetails)) {
      throw new BadCredentialsException("Invalid JWT");
    }

    return new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
  }

  private Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> message) {
    String authorizationHeader = accessor.getFirstNativeHeader(AppConstants.AUTHORIZATION_HEADER);

    if (!StringUtils.hasText(authorizationHeader)
        || !authorizationHeader.startsWith(AppConstants.BEARER_PREFIX)) {
      logger.error("Missing or invalid Authorization header on WebSocket CONNECT");
      throw new MessagingException("Missing Authorization header");
    }

    String token = authorizationHeader.substring(AppConstants.BEARER_PREFIX.length());

    try {
      Authentication authentication = authenticate(token);
      accessor.setUser(authentication);
      logger.info("WebSocket authentication successful for user: {}", authentication.getName());
      return message;
    } catch (Exception ex) {
      logger.error("WebSocket authentication failed: {}", ex.getMessage());
      throw new MessagingException("Authentication failed: " + ex.getMessage());
    }
  }
}
