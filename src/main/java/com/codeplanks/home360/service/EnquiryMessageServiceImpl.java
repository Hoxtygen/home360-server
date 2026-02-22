/* (C)2025-2026 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listingEnquiries.EnquiryMessage;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReplyDTO;
import com.codeplanks.home360.domain.listingEnquiries.PaginatedListingEnquiriesChat;
import com.codeplanks.home360.repository.EnquiryMessageRepository;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class EnquiryMessageServiceImpl implements EnquiryMessageService {

  private final EnquiryMessageRepository enquiryMessageRepository;
  private final ListingEnquiryService listingEnquiryService;
  private final MongoTemplate mongoTemplate;

  private static final Logger logger = LoggerFactory.getLogger(EnquiryMessageServiceImpl.class);

  @Override
  public EnquiryMessage addReplyMessage(
      String enquiryId, ListingEnquiryMessageReplyDTO reply, int senderId) {
    if (reply == null) {
      throw new IllegalArgumentException("Message reply cannot be null or blank");
    }

    ListingEnquiry enquiry = listingEnquiryService.getListingEnquiryById(enquiryId);

    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    EnquiryMessage newMessage =
        EnquiryMessage.builder()
            .enquiryId(enquiryId)
            .senderId(senderId)
            .receiverId(
                senderId == enquiry.getAgentId() ? enquiry.getUserId() : enquiry.getAgentId())
            .content(reply.getContent())
            .createdAt(now)
            .build();

    EnquiryMessage savedMessage = enquiryMessageRepository.save(newMessage);

    // 3. Atomic Update of lastMessageAt in the parent enquiry
    Query query = new Query(Criteria.where("_id").is(enquiryId));
    Update update = new Update().set("lastMessageAt", now);
    mongoTemplate.updateFirst(query, update, ListingEnquiry.class);

    logger.info("New message added to enquiry {}: {}", enquiryId, savedMessage.getId());

    return savedMessage;
  }

  @Override
  public PaginatedListingEnquiriesChat getEnquiryMessages(String enquiryId, int page, int size) {

    listingEnquiryService.getListingEnquiryById(enquiryId);

    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "createdAt");
    Page<EnquiryMessage> messages = enquiryMessageRepository.findByEnquiryId(enquiryId, pageable);

    return PaginatedListingEnquiriesChat.builder()
        .items(messages.getContent())
        .currentPage(messages.getNumber())
        .totalItems(messages.getTotalElements())
        .totalPages(messages.getTotalPages())
        .hasNext(messages.hasNext())
        .build();
  }
}
