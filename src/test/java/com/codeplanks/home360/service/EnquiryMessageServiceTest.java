/* (C)2025-2026 */
package com.codeplanks.home360.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.codeplanks.home360.domain.listingEnquiries.EnquiryMessage;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReplyDTO;
import com.codeplanks.home360.domain.listingEnquiries.PaginatedListingEnquiriesChat;
import com.codeplanks.home360.repository.EnquiryMessageRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class EnquiryMessageServiceTest {

  @InjectMocks private EnquiryMessageServiceImpl enquiryMessageService;
  @Mock private EnquiryMessageRepository enquiryMessageRepository;
  @Mock private ListingEnquiryService listingEnquiryService;
  @Mock private MongoTemplate mongoTemplate;

  private ListingEnquiry enquiry;
  private ListingEnquiryMessageReplyDTO replyDTO;

  @BeforeEach
  void setUp() {
    enquiry = ListingEnquiry.builder().id("enquiry123").agentId(1).userId(2).build();

    replyDTO = new ListingEnquiryMessageReplyDTO();
    replyDTO.setContent("Hello, I am interested.");
  }

  @Test
  @DisplayName("Should successfully add a reply message and update parent timestamp")
  void addReplyMessage_Success() {
    // Given
    given(listingEnquiryService.getListingEnquiryById("enquiry123")).willReturn(enquiry);
    given(enquiryMessageRepository.save(any(EnquiryMessage.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // When
    EnquiryMessage result = enquiryMessageService.addReplyMessage("enquiry123", replyDTO, 1);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo("Hello, I am interested.");
    assertThat(result.getSenderId()).isEqualTo(1);
    assertThat(result.getReceiverId()).isEqualTo(2);

    // Verify atomic update on parent
    verify(mongoTemplate)
        .updateFirst(any(Query.class), any(Update.class), eq(ListingEnquiry.class));
    verify(enquiryMessageRepository).save(any(EnquiryMessage.class));
  }

  @Test
  @DisplayName("Should throw exception when reply DTO is null")
  void addReplyMessage_NullReply_ThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> enquiryMessageService.addReplyMessage("enquiry123", null, 1));
  }

  @Test
  @DisplayName("Should return paginated messages for a valid enquiry")
  void getEnquiryMessages_Success() {
    // Given
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    List<EnquiryMessage> messageList =
        List.of(
            EnquiryMessage.builder().content("Message 1").build(),
            EnquiryMessage.builder().content("Message 2").build());
    Page<EnquiryMessage> messagePage = new PageImpl<>(messageList, pageable, 2);

    given(enquiryMessageRepository.findByEnquiryId(eq("enquiry123"), any(Pageable.class)))
        .willReturn(messagePage);

    // When
    PaginatedListingEnquiriesChat result =
        enquiryMessageService.getEnquiryMessages("enquiry123", 0, 10);

    // Then
    assertThat(result.getItems()).hasSize(2);
    assertThat(result.getTotalItems()).isEqualTo(2);
    verify(listingEnquiryService).getListingEnquiryById("enquiry123");
  }
}
