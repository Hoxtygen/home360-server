/* (C)2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.Listing;
import com.codeplanks.home360.domain.listing.RentUpdate;
import com.codeplanks.home360.domain.rental.Rental;
import com.codeplanks.home360.domain.rental.RentalDTO;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.ServiceException;
import com.codeplanks.home360.repository.RentalRepository;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
  @Autowired ListingServiceImpl listingService;
  @Autowired RentalRepository rentalRepository;
  @Autowired UserServiceImpl userService;
  Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);

  @Override
  @Transactional
  public Rental saveRentDetails(RentalDTO rentalRequest) {
    Integer userId = userService.extractUserId();
    Listing listing = listingService.findListingById(rentalRequest.getListingId());

    if (!Objects.equals(userId, listing.getAgentId())) {
      throw new AccessDeniedException("You are not authorized to create this rent information.");
    }

    AppUser renter = userService.getUser(rentalRequest.getRenterEmail());

    String rentDuration =
        calculateRentDuration(rentalRequest.getRentStartDate(), rentalRequest.getRentDueDate());

    Rental rentalDetails =
        Rental.builder()
            .renterId(renter.getId())
            .listingId(rentalRequest.getListingId())
            .rentStartDate(rentalRequest.getRentStartDate())
            .rentDueDate(rentalRequest.getRentDueDate())
            .rentDuration(rentDuration)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .agentId(listing.getAgentId())
            .build();

    try {
      Rental result = rentalRepository.save(rentalDetails);
      RentUpdate rentUpdate = new RentUpdate(listing.getId(), true);
      listingService.updateRentedListing(rentUpdate);
      listing.setRentDate(LocalDateTime.now());
      listing.setAvailableFrom(rentalRequest.getRentDueDate().plusDays(1));
      return result;
    } catch (ServiceException exception) {
      logger.error("Error saving rental details", exception);
      throw new ServiceException(exception.getMessage());
    }
  }

  private String calculateRentDuration(LocalDateTime startDate, LocalDateTime endDate) {
    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("End date cannot be before start date");
    }

    Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());

    int fullMonths = period.getMonths() + (period.getYears() * 12);
    int extraDays = period.getDays();

    return fullMonths + " months and " + extraDays + " days";
  }
}
