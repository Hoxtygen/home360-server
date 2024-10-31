/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.*;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.repository.ListingRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * @author Wasiu Idowu
 */
@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
  private final ListingRepository listingRepository;
  private final UserServiceImpl userService;

  Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);

  public Listing createListing(ListingDTO request) {
    Integer userId = userService.extractUserId();
    AppUser user = userService.getUserByUserId(userId);
    if (!user.isEnabled()) {
      throw new UnAuthorizedException(
          "You are not authorized to create a listing." + " Please confirm your email to proceed.");
    }
    ;

    Listing listing =
        Listing.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .furnishing(request.getFurnishing())
            .position(request.getPosition())
            .miscellaneous(request.getMiscellaneous())
            .address(request.getAddress())
            .agentId(userId)
            .availableFrom(request.getAvailableFrom())
            .cost(request.getCost())
            .details(request.getDetails())
            .facilityQuality(request.getFacilityQuality())
            .petsAllowed(request.getPetsAllowed())
            .apartmentInfo(request.getApartmentInfo())
            .applicationDocs(request.getApplicationDocs())
            .apartmentImages(request.getApartmentImages())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .rented(false)
            .rentDate(null)
            .build();
    Listing savedListing = listingRepository.save(listing);

    logger.info("Listing created successfully: " + savedListing);
    return savedListing;
  }

  @Override
  public List<Listing> allListings() {
    return listingRepository.findAll();
  }

  public Object deleteListing(String listingId) {
    Integer userId = userService.extractUserId();
    Listing listing = findListingById(listingId);
    Integer agentId = listing.getAgentId();
    if (!Objects.equals(agentId, userId)) {
      throw new UnAuthorizedException("You do not have the permission to delete this listing");
    }

    listingRepository.deleteById(listing.getId());

    return null;
  }

  @Override
  public ListingWithAgentInfo getListingById(String listingId) {

    Listing listing = findListingById(listingId);
    int agentId = listing.getAgentId();
    AppUser listingAgent = userService.getUserByUserId(agentId);
    ListingAgentInfo agentInfo = ListingMapper.mapToListingAgentInfo(listingAgent);
    return ListingWithAgentInfo.builder().agentInfo(agentInfo).listing(listing).build();
  }

  @Override
  public Listing findListingById(String listingId) {
    return listingRepository
        .findById(listingId)
        .orElseThrow(() -> new NotFoundException("Listing not found"));
  }

  @Override
  public PaginatedResponse<Listing> getListingsByAgentId(int page, int size) {
    Integer userId = userService.extractUserId();
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<Listing> agentListings = listingRepository.findListingsByAgentId(userId, pageable);
    return PaginatedResponse.<Listing>builder()
        .currentPage(agentListings.getNumber() + 1)
        .totalItems(agentListings.getTotalElements())
        .totalPages(agentListings.getTotalPages())
        .items(agentListings.getContent())
        .hasNext(agentListings.hasNext())
        .build();
  }

  @Override
  public PaginatedResponse<Listing> getFilteredListings(
      int page, int size, String city, int annualRent, String apartmentType) {
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<Listing> filteredListings =
        listingRepository.findAllWithFilter(city, annualRent, apartmentType, pageable);
    return PaginatedResponse.<Listing>builder()
        .currentPage(filteredListings.getNumber() + 1)
        .totalItems(filteredListings.getTotalElements())
        .totalPages(filteredListings.getTotalPages())
        .items(filteredListings.getContent())
        .hasNext(filteredListings.hasNext())
        .build();
  }

  @Override
  public Listing updateRentedListing(RentUpdate rentUpdate) {
    Integer userId = userService.extractUserId();
    Listing listingToUpdate =
        listingRepository
            .findById(rentUpdate.listingId)
            .orElseThrow(() -> new NotFoundException("Listing not found"));
    if (!Objects.equals(listingToUpdate.getAgentId(), userId)) {
      throw new AccessDeniedException("You are not authorized to update this listing.");
    }
    listingToUpdate.setRented(rentUpdate.isRented);
    if (rentUpdate.isRented) {
      listingToUpdate.setRentDate(LocalDateTime.now());
    } else {
      listingToUpdate.setRentDate(null);
    }

    Listing savedListing = listingRepository.save(listingToUpdate);
    logger.info("Listing rent status updated successfully: " + savedListing);

    return savedListing;
  }
}
