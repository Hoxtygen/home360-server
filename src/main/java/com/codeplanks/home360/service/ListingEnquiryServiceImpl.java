package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryDTO;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMapper;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UnauthorizedException;
import com.codeplanks.home360.repository.ListingEnquiryRepository;
import com.codeplanks.home360.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ListingEnquiryServiceImpl implements ListingEnquiryService {
  @Autowired
  private final AuthenticationServiceImpl authenticationService;
  private final ListingEnquiryRepository listingEnquiryRepository;

  @Override
  public ListingEnquiryDTO makeEnquiry(ListingEnquiry enquiryRequest) {
    if (AuthenticationUtils.isAuthenticated()) {
      Integer userId = authenticationService.extractUserId();
      enquiryRequest.setUserId(userId);
    }

    enquiryRequest.setCreatedAt(LocalDateTime.now());
    ListingEnquiry listingEnquiry = listingEnquiryRepository.save(enquiryRequest);
    return ListingEnquiryMapper.mapToListingEnquiryDTO(listingEnquiry);
  }

  @Override
  public PaginatedResponse<ListingEnquiry> getListingEnquiriesByAgentId(int page, int size) {
    Integer agentId = authenticationService.extractUserId();
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<ListingEnquiry> agentListingEnquiries =
            listingEnquiryRepository.findListingEnquiriesByAgentId(ListingEnquiry.class, agentId,
                    pageable);
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
    ListingEnquiry listingEnquiry = listingEnquiryRepository.findById(enquiryMessageId).orElseThrow(
            () -> new NotFoundException("Listing enquiry not found"));

    Integer userId = authenticationService.extractUserId();

    if (!listingEnquiry.getAgentId().equals(userId)) {
      throw new UnauthorizedException("Forbidden. You're not authorized to get this data");
    }
    return listingEnquiry;
  }

}
