/* (C)2025 */
package com.codeplanks.home360.websocket;

import com.codeplanks.home360.config.configProperties.WebsocketBrokerProperties;
import com.codeplanks.home360.utils.AppConstants;
import com.codeplanks.home360.validation.CurrentUserArgumentResolver;

import java.util.List;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final WebSocketAuthenticationInterceptor authenticationInterceptor;
  private final WebSocketSubscriptionAuthorizationInterceptor authorizationInterceptor;
  private final CurrentUserArgumentResolver currentUserArgumentResolver;
  private final WebsocketBrokerProperties websocketBrokerProperties;

  public WebSocketConfig(WebSocketAuthenticationInterceptor authenticationInterceptor,
                         WebSocketSubscriptionAuthorizationInterceptor authorizationInterceptor,
                         CurrentUserArgumentResolver currentUserArgumentResolver,
                         WebsocketBrokerProperties websocketBrokerProperties) {

    this.authenticationInterceptor = authenticationInterceptor;
    this.authorizationInterceptor = authorizationInterceptor;
    this.currentUserArgumentResolver = currentUserArgumentResolver;
    this.websocketBrokerProperties = websocketBrokerProperties;
  }

  private static final Logger logger =
          LoggerFactory.getLogger(WebSocketConfig.class);

  @Value("${allowed.origins}")
  private String[] allowedOrigins;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(AppConstants.WS_ENDPOINT).setAllowedOriginPatterns(allowedOrigins)
            .withSockJS();
    registry.setErrorHandler(new StompSubProtocolErrorHandler());
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config
            .setApplicationDestinationPrefixes(AppConstants.APP_PREFIX)
            .enableStompBrokerRelay(AppConstants.TOPIC_PREFIX, "/queue")
            .setRelayHost(websocketBrokerProperties.relayHost())
            .setRelayPort(websocketBrokerProperties.brokerPort())
            .setClientLogin(websocketBrokerProperties.clientLogin())
            .setClientPasscode(websocketBrokerProperties.clientPasscode())
            .setSystemLogin(websocketBrokerProperties.clientLogin())
            .setSystemPasscode(websocketBrokerProperties.clientPasscode());
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(currentUserArgumentResolver);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && accessor.getCommand() != null) {
          logger.info("Inbound STOMP command: {} destination={}",
                  accessor.getCommand(), accessor.getDestination());
        }
        return message;
      }
    }, authenticationInterceptor, authorizationInterceptor);
  }

  @Override
  public void configureClientOutboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        return message;
      }
    });
  }

}
