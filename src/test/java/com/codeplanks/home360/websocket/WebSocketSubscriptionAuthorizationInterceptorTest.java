/* (C)2025 */
package com.codeplanks.home360.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.repository.ListingEnquiryRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class WebSocketSubscriptionAuthorizationInterceptorTest {

  @InjectMocks private WebSocketSubscriptionAuthorizationInterceptor interceptor;

  @Mock private ListingEnquiryRepository listingEnquiryRepository;
  @Mock private MessageChannel channel;

  private AppUser senderUser;
  private AppUser agentUser;
  private AppUser strangerUser;
  private ListingEnquiry listingEnquiry;

  @BeforeEach
  void setUp() {
    senderUser = AppUser.builder().id(101).email("sender@example.com").build();
    agentUser = AppUser.builder().id(202).email("agent@example.com").build();
    strangerUser = AppUser.builder().id(999).email("stranger@example.com").build();

    listingEnquiry =
        ListingEnquiry.builder()
            .id("enquiry123")
            .userId(senderUser.getId())
            .agentId(agentUser.getId())
            .build();
  }

  private Message<?> createSubscribeMessage(String destination, AppUser user) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
    accessor.setDestination(destination);
    if (user != null) {
      accessor.setUser(
          new UsernamePasswordAuthenticationToken(
              user, null, Collections.emptyList()));
    }
    return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
  }

  @Test
  @DisplayName("Allow subscription if user is sender")
  void whenUserIsSender_thenAllowSubscription() {
    Message<?> message = createSubscribeMessage("/topic/public.enquiry123", senderUser);
    when(listingEnquiryRepository.findById("enquiry123")).thenReturn(Optional.of(listingEnquiry));

    Message<?> result = interceptor.preSend(message, channel);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(message);
  }

  @Test
  @DisplayName("Allow subscription if user is agent")
  void whenUserIsAgent_thenAllowSubscription() {
    Message<?> message = createSubscribeMessage("/topic/public.enquiry123", agentUser);
    when(listingEnquiryRepository.findById("enquiry123")).thenReturn(Optional.of(listingEnquiry));

    Message<?> result = interceptor.preSend(message, channel);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(message);
  }

  @Test
  @DisplayName("Deny subscription if user is unauthorized")
  void whenUserIsUnauthorized_thenThrowAccessDeniedException() {
    Message<?> message = createSubscribeMessage("/topic/public.enquiry123", strangerUser);
    when(listingEnquiryRepository.findById("enquiry123")).thenReturn(Optional.of(listingEnquiry));

    assertThatThrownBy(() -> interceptor.preSend(message, channel))
        .isInstanceOf(AccessDeniedException.class)
        .hasMessageContaining("You are not authorized to view this conversation");
  }

  @Test
  @DisplayName("Deny subscription if user is not authenticated")
  void whenUserIsNotAuthenticated_thenThrowAccessDeniedException() {
    Message<?> message = createSubscribeMessage("/topic/public.enquiry123", null);

    assertThatThrownBy(() -> interceptor.preSend(message, channel))
        .isInstanceOf(AccessDeniedException.class)
        .hasMessageContaining("User is not authenticated");
  }

  @Test
  @DisplayName("Throw exception if enquiry not found")
  void whenEnquiryNotFound_thenThrowMessagingException() {
    Message<?> message = createSubscribeMessage("/topic/public.unknown123", senderUser);
    when(listingEnquiryRepository.findById("unknown123")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> interceptor.preSend(message, channel))
        .isInstanceOf(MessagingException.class)
        .hasMessageContaining("Enquiry not found");
  }

  @Test
  @DisplayName("Ignore unrelated topics")
  void whenTopicIsUnrelated_thenPassThrough() {
    Message<?> message = createSubscribeMessage("/topic/some.other.topic", strangerUser);

    Message<?> result = interceptor.preSend(message, channel);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(message);
    verifyNoInteractions(listingEnquiryRepository);
  }

  @Test
  @DisplayName("Handle malformed topic correctly")
  void whenTopicIsMalformed_thenPassThrough() {
    // "/topic/public" should not match "/topic/public.{enquiryId}" because of the missing dot separator
    Message<?> message = createSubscribeMessage("/topic/public", strangerUser);

    Message<?> result = interceptor.preSend(message, channel);

    assertThat(result).isNotNull();
    verifyNoInteractions(listingEnquiryRepository);
  }
}
