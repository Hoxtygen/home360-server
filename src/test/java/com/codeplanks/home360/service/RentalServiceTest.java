/* (C)2025 */
package com.codeplanks.home360.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.codeplanks.home360.domain.listing.Listing;
import com.codeplanks.home360.domain.rental.Rental;
import com.codeplanks.home360.domain.rental.RentalDTO;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.ServiceException;
import com.codeplanks.home360.repository.RentalRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RentalServiceTest {
  @InjectMocks RentalServiceImpl rentalService;
  @Mock UserServiceImpl userService;
  @Mock ListingServiceImpl listingService;
  @Mock RentalRepository rentalRepository;

  @Test
  @DisplayName("Create rent details successfully")
  void givenValidRentDataWhenSavedThenRentInfoIsCreated() {
    // Given
    String userEmail = "john_doe@yopmail.com";
    int agentId = 2;
    String listingId = "eda8439034fac0a23";

    AppUser john = new AppUser();
    john.setId(100);

    RentalDTO rentalDTO =
        new RentalDTO(
            LocalDateTime.now().minusMonths(2),
            LocalDateTime.now().plusMonths(4),
            listingId,
            userEmail,
            LocalDateTime.now(),
            LocalDateTime.now());

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(agentId);

    given(userService.extractUserId()).willReturn(agentId);
    given(userService.getUser(userEmail)).willReturn(john);
    given(listingService.findListingById(listingId)).willReturn(listing);
    Rental newRental = new Rental();
    newRental.setId("679925bda18e0725fb9eb3e2");

    // When
    when(rentalRepository.save(any(Rental.class))).thenReturn(newRental);
    Rental result = rentalService.saveRentDetails(rentalDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("679925bda18e0725fb9eb3e2");
    verify(rentalRepository, times(1)).save(any(Rental.class));
  }

  @Test
  @DisplayName("Date error")
  void givenInvalidRentDateWhenSavedThenThrowIllegalArgumentException() {
    // Given
    String userEmail = "john_doe@yopmail.com";
    int agentId = 2;
    String listingId = "eda8439034fac0a23";

    AppUser john = new AppUser();
    john.setId(100);

    RentalDTO rentalDTO =
        new RentalDTO(
            LocalDateTime.now().plusMonths(2),
            LocalDateTime.now().minusMonths(4),
            listingId,
            userEmail,
            LocalDateTime.now(),
            LocalDateTime.now());

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(agentId);

    given(userService.extractUserId()).willReturn(agentId);
    given(userService.getUser(userEmail)).willReturn(john);
    given(listingService.findListingById(listingId)).willReturn(listing);
    Rental newRental = new Rental();
    newRental.setId("679925bda18e0725fb9eb3e2");

    // When
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> rentalService.saveRentDetails(rentalDTO));

    // Then
    assertThat(exception.getMessage()).isEqualTo("End date cannot be before start date");
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  @DisplayName("Non-existent listing ID")
  void givenInvalidListingIdWhenSavedThenThrowNotFoundException() {
    // Given
    String userEmail = "john_doe@yopmail.com";
    int agentId = 2;
    String listingId = "eda8439034fac0a23";

    AppUser john = new AppUser();
    john.setId(100);

    RentalDTO rentalDTO =
        new RentalDTO(
            LocalDateTime.now().minusMonths(2),
            LocalDateTime.now().plusMonths(4),
            listingId,
            userEmail,
            LocalDateTime.now(),
            LocalDateTime.now());

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(agentId);

    given(userService.extractUserId()).willReturn(agentId);
    given(listingService.findListingById(listingId))
        .willThrow(new NotFoundException("Listing not found"));
    Rental newRental = new Rental();
    newRental.setId("679925bda18e0725fb9eb3e2");

    // When
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> rentalService.saveRentDetails(rentalDTO));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Listing not found");
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  @DisplayName("Invalid agent ID")
  void givenInvalidAgentIdWhenSavedThenThrowAccessDeniedException() {
    // Given
    String userEmail = "john_doe@yopmail.com";
    int agentId = 2;
    String listingId = "eda8439034fac0a23";

    AppUser john = new AppUser();
    john.setId(100);

    RentalDTO rentalDTO =
        new RentalDTO(
            LocalDateTime.now().minusMonths(2),
            LocalDateTime.now().plusMonths(4),
            listingId,
            userEmail,
            LocalDateTime.now(),
            LocalDateTime.now());

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(agentId);

    given(userService.extractUserId()).willReturn(5);
    given(listingService.findListingById(listingId)).willReturn(listing);
    Rental newRental = new Rental();
    newRental.setId("679925bda18e0725fb9eb3e2");

    // When
    AccessDeniedException exception =
        assertThrows(AccessDeniedException.class, () -> rentalService.saveRentDetails(rentalDTO));

    // Then
    assertThat(exception.getMessage())
        .isEqualTo("You are not authorized to create this rent information.");
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  @DisplayName("Non-existent renter email")
  void givenInvalidRenterEmailAddressWhenSavedThenThrowNotFoundException() {
    // Given
    String userEmail = "john_doe@yopmail.com";
    int agentId = 2;
    String listingId = "eda8439034fac0a23";

    AppUser john = new AppUser();
    john.setId(100);

    RentalDTO rentalDTO =
        new RentalDTO(
            LocalDateTime.now().minusMonths(2),
            LocalDateTime.now().plusMonths(4),
            listingId,
            userEmail,
            LocalDateTime.now(),
            LocalDateTime.now());

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(agentId);

    given(userService.extractUserId()).willReturn(agentId);
    given(listingService.findListingById(listingId)).willReturn(listing);
    given(userService.getUser(userEmail)).willThrow(new NotFoundException("User not found"));
    Rental newRental = new Rental();
    newRental.setId("679925bda18e0725fb9eb3e2");

    // When
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> rentalService.saveRentDetails(rentalDTO));

    // Then
    assertThat(exception.getMessage()).isEqualTo("User not found");
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  @DisplayName("Database error")
  void givenRepositorySaveFailsWhenSavingRentThenThrowServiceException() {
    // Given
    String userEmail = "john_doe@yopmail.com";
    int agentId = 2;
    String listingId = "eda8439034fac0a23";

    AppUser john = new AppUser();
    john.setId(100);

    RentalDTO rentalDTO =
        new RentalDTO(
            LocalDateTime.now().minusMonths(2),
            LocalDateTime.now().plusMonths(4),
            listingId,
            userEmail,
            LocalDateTime.now(),
            LocalDateTime.now());

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(agentId);

    given(userService.extractUserId()).willReturn(agentId);
    given(userService.getUser(userEmail)).willReturn(john);
    given(listingService.findListingById(listingId)).willReturn(listing);
    Rental newRental = new Rental();
    newRental.setId("679925bda18e0725fb9eb3e2");
    given(rentalRepository.save(any(Rental.class)))
        .willThrow(new ServiceException("Database " + "error"));

    // When
    ServiceException exception =
        assertThrows(ServiceException.class, () -> rentalService.saveRentDetails(rentalDTO));

    // Then
    assertEquals("Database error", exception.getMessage());
  }
}
