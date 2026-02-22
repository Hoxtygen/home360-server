/* (C)2024-2026 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.Listing;
import com.codeplanks.home360.domain.listingEnquiries.*;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.ListingEnquiryRepository;
import com.codeplanks.home360.utils.AuthenticationUtils;
import com.mongodb.client.result.UpdateResult;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
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
        @CacheEvict(value = "agentListingEnquiries", key = "#enquiryRequest.agentId + " + "'::*'")
      })
  @Override
  public ListingEnquiry makeEnquiry(ListingEnquiryDTO enquiryRequest) {
    if (authenticationUtils.isAuthenticated()) {
      Integer userId = userService.extractUserId();
      enquiryRequest.setUserId(userId);
    }

    listingService.findListingById(enquiryRequest.getListingId());

    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    enquiryRequest.setCreatedAt(now);
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
            .createdAt(now)
            .lastMessageAt(now)
            .unreadCountByAgent(1)
            .unreadCountByInquirer(0)
            .status(EnquiryStatus.PENDING)
            .build();
    return listingEnquiryRepository.save(newListingEnquiry);
  }

  @Cacheable(
      value = "agentListingEnquiries",
      key =
          "#agentId + '::senderId::' + #senderId + '::status::' + #status + '::page::' + #page +"
              + " '::size::' + #size")
  @Override
  public PaginatedListingEnquiriesResponse getListingEnquiries(
      int page, int size, Integer senderId, int agentId, EnquiryStatus status) {
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "last_message_at");
    Page<ListingEnquiry> agentListingEnquiries =
        listingEnquiryRepository.findListingEnquiries(agentId, senderId, status, pageable);
    return PaginatedListingEnquiriesResponse.<ListingEnquiry>builder()
        .currentPage(agentListingEnquiries.getNumber() + 1)
        .totalItems(agentListingEnquiries.getTotalElements())
        .totalPages(agentListingEnquiries.getTotalPages())
        .items(agentListingEnquiries.getContent())
        .hasNext(agentListingEnquiries.hasNext())
        .build();
  }

  @Cacheable(
      value = "listingEnquiry",
      key = "#enquiryMessageId + '::' + #root.target.userService.extractUserId()")
  @Override
  public ListingEnquiry getListingEnquiryById(String enquiryMessageId) {
    if (enquiryMessageId == null) {
      throw new IllegalArgumentException("Enquiry ID cannot be null or empty");
    }
    ListingEnquiry listingEnquiry =
        listingEnquiryRepository
            .findById(enquiryMessageId)
            .orElseThrow(() -> new NotFoundException("Listing enquiry not found"));

    validateUserAuthorization(listingEnquiry, userService.extractUserId());
    return listingEnquiry;
  }

  @Override
  public Boolean markEnquiryAsRead(String enquiryId) {
    ListingEnquiry enquiry = getListingEnquiryById(enquiryId);
    Integer userId = userService.extractUserId();

    Query query = new Query(Criteria.where("_id").is(enquiryId));
    Update update = new Update();

    if (Objects.equals(userId, enquiry.getAgentId())) {
      update.set("unreadCountByAgent", 0);
      // Transition from PENDING to ACTIVE when agent acknowledges
      if (enquiry.getStatus() == EnquiryStatus.PENDING) {
        update.set("status", EnquiryStatus.ACTIVE);
      }
    } else if (Objects.equals(userId, enquiry.getUserId())) {
      update.set("unreadCountByInquirer", 0);
    } else {
      throw new AccessDeniedException("You are not authorized to mark this conversation as read");
    }

    UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ListingEnquiry.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Cacheable(
      value = "enquiriesByListingId",
      key = "#listingId + '::' + #root.target.userService.extractUserId()")
  @Override
  public List<ListingEnquiry> getEnquiriesByListingId(String listingId) {
    Integer userId = userService.extractUserId();
    Listing listing = listingService.findListingById(listingId);
    if (!Objects.equals(userId, listing.getAgentId())) {
      throw new AccessDeniedException("You are not authorized to view these enquiries.");
    }
    return listingEnquiryRepository.findByListingId(listingId);
  }

  @Override
  public Integer getTotalUnreadCount() {
    Integer userId = userService.extractUserId();
    if (userId == null) {
      return 0;
    }

    // Sum unread messages as an Agent
    int agentUnread =
        listingEnquiryRepository.findAgentUnreadCounts(userId).stream()
            .mapToInt(ListingEnquiry::getUnreadCountByAgent)
            .sum();

    // Sum unread messages as an Inquirer
    int inquirerUnread =
        listingEnquiryRepository.findInquirerUnreadCounts(userId).stream()
            .mapToInt(ListingEnquiry::getUnreadCountByInquirer)
            .sum();

    return agentUnread + inquirerUnread;
  }

  private void validateUserAuthorization(ListingEnquiry listingEnquiry, Integer userId) {
    boolean isAgent = userId != null && userId.equals(listingEnquiry.getAgentId());
    boolean isInquirer =
        userId != null
            && listingEnquiry.getUserId() != null
            && userId.equals(listingEnquiry.getUserId());
    if (!isAgent && !isInquirer) {
      throw new AccessDeniedException("Forbidden. You're not authorized to access this data");
    }
  }
}
