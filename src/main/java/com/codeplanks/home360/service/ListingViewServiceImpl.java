/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.Listing;
import com.codeplanks.home360.domain.listingView.ListingView;
import com.codeplanks.home360.domain.listingView.ListingViewDTO;
import com.codeplanks.home360.repository.ListingViewRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListingViewServiceImpl implements ListingViewService {
  private final ListingViewRepository viewRepository;
  private final ListingServiceImpl listingService;
  private final UserServiceImpl userService;

  @Override
  public ListingView saveListingView(ListingViewDTO request) {
    listingService.findListingById(request.getListingId());
    ListingView listingView =
        ListingView.builder()
            .listingId(request.getListingId())
            .timestamp(request.getTimestamp())
            .createdAt(LocalDateTime.now())
            .build();
    return viewRepository.save(listingView);
  }

  @Override
  public List<ListingView> getAllListingViews() {
    return viewRepository.findAll();
  }

  @Override
  public List<ListingView> getViewsByListingId(String listingId) {
    int userId = userService.extractUserId();
    Listing listing = listingService.findListingById(listingId);
    if (listing.getAgentId() != userId) {
      throw new AccessDeniedException("You are forbidden from accessing this data");
    }
    return viewRepository.findByListingId(listingId);
  }
}
