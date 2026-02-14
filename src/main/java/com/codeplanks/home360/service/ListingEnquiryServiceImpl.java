/* (C)2024-2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.Listing;
import com.codeplanks.home360.domain.listingEnquiries.*;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.ListingEnquiryRepository;
import com.codeplanks.home360.utils.AuthenticationUtils;
import com.mongodb.client.result.UpdateResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

@CacheConfig(cacheNames = "ListingEnquiriesCache")
@Service
@RequiredArgsConstructor
@Validated
public class ListingEnquiryServiceImpl implements ListingEnquiryService {
  private final ListingEnquiryRepository listingEnquiryRepository;
  private final MongoTemplate mongoTemplate;
  private final UserServiceImpl userService;
  private final ListingServiceImpl listingService;
  private final AuthenticationUtils authenticationUtils;

  @Caching(
      evict = {
        @CacheEvict(value = "enquiriesByListingId", key = "#enquiryRequest.listingId"),
        @CacheEvict(value = "agentListingEnquiries", key = "#enquiryRequest.agentId + '::*'")
      })
  @Override
  public ListingEnquiry makeEnquiry(ListingEnquiryDTO enquiryRequest) {
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

  @Cacheable(
      value = "agentListingEnquiries",
      key =
          "#agentId + '::senderId::' + #senderId + '::page::' + #page + '::size::' + #size")
  @Override
  public PaginatedListingEnquiriesResponse getListingEnquiries(
      int page, int size, Integer senderId, int agentId) {
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<ListingEnquiry> agentListingEnquiries =
        listingEnquiryRepository.findListingEnquiries(agentId, senderId, pageable);
    return PaginatedListingEnquiriesResponse.<ListingEnquiry>builder()
        .currentPage(agentListingEnquiries.getNumber() + 1)
        .totalItems(agentListingEnquiries.getTotalElements())
        .totalPages(agentListingEnquiries.getTotalPages())
        .items(agentListingEnquiries.getContent())
        .hasNext(agentListingEnquiries.hasNext())
        .build();
  }

  @Cacheable(value = "listingEnquiry", key = "#enquiryMessageId")
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

  @Caching(evict = {@CacheEvict(value = "listingEnquiry", key = "#enquiryMessageId")})
  @Override
  public ListingEnquiryMessageReply addReplyMessage(
      String enquiryMessageId, ListingEnquiryMessageReplyDTO reply, int senderId) {
    if (reply == null) {
      throw new IllegalArgumentException("Message reply cannot be null or blank");
    }

    // 1. Create the reply object
    ListingEnquiryMessageReply newReply =
        ListingEnquiryMessageReply.builder()
            .agentId(reply.getAgentId())
            .enquirerId(reply.getEnquirerId())
            .senderId(senderId)
            .content(reply.getContent())
            .id(UUID.randomUUID().toString())
            .createdAt(LocalDateTime.now())
            .build();

    // 2. Create a single, atomic, authorizing query
    Query query =
        new Query(
            Criteria.where("_id")
                .is(enquiryMessageId)
                .orOperator(
                    Criteria.where("agentId").is(senderId), Criteria.where("userId").is(senderId)));

    // 3. Create the update operation
    Update update = new Update().push("replies", newReply);

    // 4. Execute the update
    UpdateResult result = mongoTemplate.updateFirst(query, update, ListingEnquiry.class);

    // 5. Check if the update was successful
    if (result.getMatchedCount() == 0) {
      // This means either the enquiry doesn't exist OR the user is not authorized
      throw new AccessDeniedException("Enquiry not found or you are not authorized to reply to it.");
    }

    return newReply;
  }

  @Cacheable(value = "enquiriesByListingId", key = "#listingId")
  @Override
  public List<ListingEnquiry> getEnquiriesByListingId(String listingId) {
    Integer userId = userService.extractUserId();
    Listing listing = listingService.findListingById(listingId);
    if (!Objects.equals(userId, listing.getAgentId())) {
      throw new AccessDeniedException("You are not authorized to view these enquiries.");
    }
    return listingEnquiryRepository.findByListingId(listingId);
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
}
