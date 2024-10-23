/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.*;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.ListingEnquiryRepository;
import com.codeplanks.home360.utils.AuthenticationUtils;
import com.mongodb.client.result.UpdateResult;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class ListingEnquiryServiceImpl implements ListingEnquiryService {
  private final ListingEnquiryRepository listingEnquiryRepository;
  private final MongoTemplate mongoTemplate;
  private final UserServiceImpl userService;
  private final ListingServiceImpl listingService;
  private final AuthenticationUtils authenticationUtils;

  @Override
  public ListingEnquiry makeEnquiry(@Valid ListingEnquiryDTO enquiryRequest) {
    if (authenticationUtils.isAuthenticated()) {
      Integer userId = userService.extractUserId();
      enquiryRequest.setUserId(userId);
    }

    listingService.findListingById(enquiryRequest.getListingId());

    enquiryRequest.setCreatedAt(LocalDateTime.now());
    ListingEnquiry newListingEnquiry =
        ListingEnquiry.builder()
            .firstName(enquiryRequest.getFirstName())
            .lastName(enquiryRequest.getLastName())
            .email(enquiryRequest.getEmail())
            .phoneNumber(enquiryRequest.getPhoneNumber())
            .location(enquiryRequest.getLocation())
            .salutation(enquiryRequest.getSalutation())
            .message(enquiryRequest.getMessage())
            .employmentStatus(enquiryRequest.getEmploymentStatus())
            .pets(enquiryRequest.getPets())
            .commercialPurpose(enquiryRequest.getCommercialPurpose())
            .listingId(enquiryRequest.getListingId())
            .agentId(enquiryRequest.getAgentId())
            .userId(enquiryRequest.getUserId())
            .createdAt(LocalDateTime.now())
            .build();
    return listingEnquiryRepository.save(newListingEnquiry);
  }

  @Override
  public PaginatedResponse<ListingEnquiry> getListingEnquiries(
      int page, int size, Integer senderId) {
    Integer agentId = userService.extractUserId();
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<ListingEnquiry> agentListingEnquiries =
        listingEnquiryRepository.findListingEnquiries(
            ListingEnquiry.class, agentId, senderId, pageable);
    return PaginatedResponse.<ListingEnquiry>builder()
        .currentPage(agentListingEnquiries.getNumber() + 1)
        .totalItems(agentListingEnquiries.getTotalElements())
        .totalPages(agentListingEnquiries.getTotalPages())
        .items(agentListingEnquiries.getContent())
        .hasNext(agentListingEnquiries.hasNext())
        .build();
  }

  @Override
  public ListingEnquiry getListingEnquiryById(String enquiryMessageId) {
    if (enquiryMessageId == null) {
      throw new IllegalArgumentException("Enquiry ID cannot be null or empty");
    }
    ListingEnquiry listingEnquiry =
        listingEnquiryRepository
            .findById(enquiryMessageId)
            .orElseThrow(() -> new NotFoundException("Listing enquiry not found"));

    validateUserAuthorization(listingEnquiry);
    return listingEnquiry;
  }

  @Override
  public Boolean markMessageAsRead(String enquiryMessageId) {
    Query query = createQuery(enquiryMessageId);
    validateEnquiryExists(query);
    ListingEnquiry listingEnquiry = mongoTemplate.findOne(query, ListingEnquiry.class);
    if (listingEnquiry != null) {
      validateUserAuthorization(listingEnquiry);
    }
    return updateMessageAsRead(query);
  }

  @Override
  public ListingEnquiryMessageReply addReplyMessage(
      String enquiryMessageId, ListingEnquiryMessageReplyDTO reply) {
    if (reply == null) {
      throw new IllegalArgumentException("Message reply cannot be null or blank");
    }
    validateUserIds(reply.getSenderId(), reply.getReceiverId());
    Query query = createQuery(enquiryMessageId);
    return addEnquiryReply(query, reply);
  }

  private Query createQuery(String enquiryMessageId) {
    return new Query(Criteria.where("_id").is(enquiryMessageId));
  }

  private void validateEnquiryExists(Query query) {
    boolean exists = mongoTemplate.exists(query, ListingEnquiry.class);
    if (!exists) {
      throw new NotFoundException("Listing enquiry with the given ID was not found");
    }
  }

  private void validateUserAuthorization(ListingEnquiry listingEnquiry) {
    Integer userId = userService.extractUserId();
    boolean isAgent = userId != null && userId.equals(listingEnquiry.getAgentId());
    boolean isInquirer =
        userId != null
            && listingEnquiry.getUserId() != null
            && userId.equals(listingEnquiry.getUserId());
    if (!isAgent && !isInquirer) {
      throw new AccessDeniedException("Forbidden. You're not authorized to access this data");
    }
  }

  private Boolean updateMessageAsRead(Query query) {
    Update update = new Update().set("read", true);
    UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ListingEnquiry.class);
    return updateResult.getModifiedCount() > 0;
  }

  private ListingEnquiryMessageReply addEnquiryReply(
      Query query, ListingEnquiryMessageReplyDTO messageReply) {
    if (messageReply == null) {
      throw new IllegalArgumentException("Message reply cannot be null");
    }
    ListingEnquiryMessageReply reply =
        ListingEnquiryMessageReply.builder()
            .senderId(messageReply.getSenderId())
            .receiverId(messageReply.getReceiverId())
            .content(messageReply.getContent())
            .id(UUID.randomUUID().toString())
            .createdAt(LocalDateTime.now())
            .build();

    Update update = new Update().push("replies", reply);
    UpdateResult result = mongoTemplate.updateFirst(query, update, ListingEnquiry.class);
    if (result.getMatchedCount() == 0) {
      throw new NotFoundException("EnquiryId does not exist");
    }
    if (result.getModifiedCount() == 0) {
      throw new NotFoundException("EnquiryId");
    }

    return reply;
  }

  private void validateUserIds(Integer senderId, Integer receiverId) {
    userService.getUserByUserId(senderId);
    userService.getUserByUserId(receiverId);
  }
}
