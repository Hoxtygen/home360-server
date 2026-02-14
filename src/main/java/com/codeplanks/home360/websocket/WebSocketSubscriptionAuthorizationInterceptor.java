/* (C)2025 */
package com.codeplanks.home360.websocket;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.repository.ListingEnquiryRepository;
import com.codeplanks.home360.utils.AppConstants;
import java.util.Map;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class WebSocketSubscriptionAuthorizationInterceptor implements ChannelInterceptor {

  private static final Logger logger =
      LoggerFactory.getLogger(WebSocketSubscriptionAuthorizationInterceptor.class);

  private final ListingEnquiryRepository listingEnquiryRepository;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();
  
  // Define the pattern we are protecting
  private static final String ENQUIRY_TOPIC_PATTERN = AppConstants.TOPIC_PREFIX + "/public.{enquiryId}";

  public WebSocketSubscriptionAuthorizationInterceptor(
      ListingEnquiryRepository listingEnquiryRepository) {
    this.listingEnquiryRepository = listingEnquiryRepository;
  }

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      validateSubscription(accessor);
    }

    return message;
  }

  private void validateSubscription(StompHeaderAccessor accessor) {
    String destination = accessor.getDestination();
    
    // Only intercept topics matching our protected pattern
    if (destination == null || !pathMatcher.match(ENQUIRY_TOPIC_PATTERN, destination)) {
      return;
    }

    // Extract the variable using Spring's standard matcher
    Map<String, String> variables = pathMatcher.extractUriTemplateVariables(ENQUIRY_TOPIC_PATTERN, destination);
    String enquiryId = variables.get("enquiryId");

    logger.debug("Validating subscription for enquiry ID: {}", enquiryId);

    Authentication authentication = (Authentication) accessor.getUser();
    if (authentication == null || !(authentication.getPrincipal() instanceof AppUser)) {
      throw new AccessDeniedException("User is not authenticated");
    }

    AppUser currentUser = (AppUser) authentication.getPrincipal();
    ListingEnquiry enquiry =
        listingEnquiryRepository
            .findById(enquiryId)
            .orElseThrow(() -> new MessagingException("Enquiry not found"));

    boolean isAuthorized =
        currentUser.getId().equals(enquiry.getUserId())
            || currentUser.getId().equals(enquiry.getAgentId());

    if (!isAuthorized) {
      logger.warn(
          "User {} attempted to subscribe to unauthorized enquiry {}",
          currentUser.getEmail(),
          enquiryId);
      throw new AccessDeniedException("You are not authorized to view this conversation");
    }
    logger.info("User {} authorized for enquiry {}", currentUser.getEmail(), enquiryId);
  }
}
