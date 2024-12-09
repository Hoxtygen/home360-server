/* (C)2024 */
package com.codeplanks.home360.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.codeplanks.home360.domain.listing.*;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.repository.ListingRepository;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ListingServiceTest {
  @InjectMocks ListingServiceImpl listingService;
  @Mock ListingRepository listingRepository;
  @Mock UserServiceImpl userService;

  private AppUser john;

  private ListingDTO listingDTO;

  @BeforeEach
  void setUp() {}

  @Test
  @DisplayName("create listing successfully")
  void givenValidRequestWhenSavedThenListingIsCreated() {
    // Given
    john =
        AppUser.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .email("John_doe@example.com")
            .address("221B Baker street")
            .phoneNumber("080212345678")
            .password("1245678")
            .role(Role.USER)
            .isEnabled(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    listingDTO =
        ListingDTO.builder()
            .title("2-bedroom flat in Lagos Island")
            .description("Lorem ipsum dolor sit amet")
            .furnishing("Lorem ipsum dolor")
            //            .agentId(1)
            .position("Lorem picsum")
            .miscellaneous("Another lorem in the making")
            .address(new Address("Orilonise stree", "23H", "Lagos", "Lagos", "Alimosho"))
            .availableFrom(LocalDateTime.now().minusDays(5))
            .cost(new ListingCost(500000, 50000, 50000, 50000))
            .details(
                List.of(
                    "Good electricity",
                    "Closer to the airport",
                    "All embassy within 10 " + "minute drive"))
            .facilityQuality(FacilityQuality.UPSCALE)
            .petsAllowed(PetsAllowed.TO_BE_ARRANGED)
            .apartmentInfo(new ApartmentInfo(3, 2, 2, ApartmentType.APARTMENT))
            .applicationDocs(List.of("Tax Id", "Evidence of income"))
            .apartmentImages(List.of("lorempicsum.com/photos/4040", "lorempicsum.com/photos/400"))
            .build();
    Integer agentId = 1;
    given(userService.extractUserId()).willReturn(agentId);
    given(userService.getUserByUserId(agentId)).willReturn(john);
    Listing newListing = new Listing();
    newListing.setId("adjkjkejke8eji4ruehi");

    // When
    when(listingRepository.save(any(Listing.class))).thenReturn(newListing);
    Listing result = listingService.createListing(listingDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("adjkjkejke8eji4ruehi");
    verify(listingRepository, times(1)).save(any(Listing.class));
  }

  @Test
  @DisplayName("Disabled user")
  void givenDisabledUserWhenCreateListingThenThrowUnAuthorizedException() {
    // Given
    AppUser user = new AppUser();
    user.setEnabled(false);

    ListingDTO request = new ListingDTO();

    Integer agentId = 1;
    given(userService.extractUserId()).willReturn(agentId);
    given(userService.getUserByUserId(agentId)).willReturn(user);

    // When
    UnAuthorizedException exception =
        assertThrows(UnAuthorizedException.class, () -> listingService.createListing(request));
    // Then
    assertThat(exception.getMessage())
        .isEqualTo(
            "You are not authorized to create a listing. Please confirm your email to proceed.");
  }

  @Test
  @DisplayName("Invalid user id")
  void givenInvalidUserIdWhenCreateListingThenThrowException() {
    // Given
    given(userService.extractUserId()).willReturn(1);
    given(userService.getUserByUserId(1)).willThrow(new NotFoundException("User not found"));

    // When
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> listingService.createListing(new ListingDTO()));

    // Then
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  @DisplayName("Database error")
  void givenRepositorySaveFailsWhenCreateListingThenThrowRuntimeException() {
    // Given
    ListingDTO request = new ListingDTO();
    AppUser user = new AppUser();
    user.setEnabled(true);

    given(userService.extractUserId()).willReturn(1);
    given(userService.getUserByUserId(1)).willReturn(user);
    given(listingRepository.save(any(Listing.class)))
        .willThrow(new RuntimeException("Database error"));

    // When
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> listingService.createListing(request));

    // Then
    assertEquals("Database error", exception.getMessage());
  }

  @Test
  @DisplayName("Get all listings")
  void givenListingsExistWhenAllListingsThenReturnListOfListings() {
    // Given
    Listing listing1 = new Listing();
    Listing listing2 = new Listing();
    List<Listing> listings = Arrays.asList(listing1, listing2);

    given(listingRepository.findAll()).willReturn(listings);

    // When
    List<Listing> result = listingService.allListings();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(listingRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Empty listings")
  void givenNoListingsExistWhenAllListingsThenReturnEmptyList() {
    // Given
    given(listingRepository.findAll()).willReturn(Collections.emptyList());

    // When
    List<Listing> result = listingService.allListings();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(listingRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Delete listing successfully")
  void deleteListing() {
    // Given
    Integer userId = 1;
    String listingId = "r83yuwihfuehjejndjbhd";
    Listing listingToDelete = new Listing();
    listingToDelete.setId(listingId);
    listingToDelete.setAgentId(userId);
    given(listingRepository.findById(listingId)).willReturn(Optional.of(listingToDelete));
    given(userService.extractUserId()).willReturn(userId);

    // When
    Object result = listingService.deleteListing(listingId);

    // Then
    assertNull(result);
    verify(listingRepository, times(1)).deleteById(listingId);
    verify(listingRepository, times(1)).findById(listingId);
  }

  @Test
  @DisplayName("Invalid agent deletion")
  void givenUserIsNotAgentWhenDeleteListingThenThrowsUnAuthorizedException() {
    // Given
    String listingId = "123";
    int userId = 1;
    int agentId = 2;
    Listing listing = new Listing();
    listing.setAgentId(agentId);
    given(userService.extractUserId()).willReturn(userId);
    given(listingRepository.findById(listingId)).willReturn(Optional.of(listing));

    // When
    UnAuthorizedException exception =
        assertThrows(UnAuthorizedException.class, () -> listingService.deleteListing(listingId));

    // Then
    assertEquals("You do not have the permission to delete this listing", exception.getMessage());
    verify(listingRepository, never()).deleteById(anyString());
  }

  @Test
  @DisplayName("Listing not found")
  void givenListingNotFoundWhenDeleteListingThenDoesNothing() {
    // Given
    String listingId = "123";
    int userId = 1;

    given(userService.extractUserId()).willReturn(userId);
    given(listingRepository.findById(listingId)).willReturn(Optional.empty());

    // When
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> listingService.deleteListing(listingId));

    // Then
    assertEquals("Listing not found", exception.getMessage());
    verify(listingRepository, never()).deleteById(anyString());
  }

  @Test
  @DisplayName("Get listing with agent info successfully")
  void givenValidListingIdWhenRequestForListingThenReturnListingWithAgentInfo() {
    // Given
    Integer userId = 1;
    String listingId = "12345";
    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(userId);
    AppUser user = new AppUser();
    user.setId(userId);
    given(listingRepository.findById(listingId)).willReturn(Optional.of(listing));
    given(userService.getUserByUserId(userId)).willReturn(user);

    // When
    ListingWithAgentInfo result = listingService.getListingById(listingId);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getListing().getAgentId());
    verify(listingRepository, times(1)).findById(listingId);
  }

  @Test
  @DisplayName("Not found listing Id")
  void givenNonExistentListingIdWhenUserRequestForListingThenThrowNotFoundExceptions() {
    // Given
    String listingId = "1234";
    given(listingRepository.findById(listingId)).willReturn(Optional.empty());
    // When
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> listingService.getListingById(listingId));

    // Then
    assertEquals("Listing not found", exception.getMessage());
  }

  @Test
  @DisplayName("Get filtered listings successfully")
  void givenValidQueriesWhenUserFilterListingsThenReturnResult() {
    // Given
    int page = 0;
    int size = 20;
    String city = "Lagos";
    int annualRent = 200000;
    String apartmentType = "DUPLEX";
    Listing listing1 = new Listing();
    listing1.setId("2746bdfrhkjfdhfjdd");
    listing1.setApartmentInfo(new ApartmentInfo(3, 2, 2, ApartmentType.DUPLEX));
    listing1.setCost(new ListingCost(200000, 20000, 50000, 20000));

    Listing listing2 = new Listing();
    listing2.setId("kdjkfjieu8y48uu3t");
    listing2.setApartmentInfo(new ApartmentInfo(4, 2, 3, ApartmentType.DUPLEX));
    listing2.setCost(new ListingCost(350000, 35000, 50000, 35000));

    List<Listing> listings = List.of(listing1, listing2);
    Page<Listing> mockPage = new PageImpl<>(listings, PageRequest.of(page, size), 2);
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    given(listingRepository.findAllWithFilter(city, annualRent, apartmentType, pageable))
        .willReturn(mockPage);

    // When

    PaginatedResponse<Listing> result =
        listingService.getFilteredListings(page, size, city, annualRent, apartmentType);
    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.getTotalItems()).isEqualTo(2),
        () -> assertThat(result.getCurrentPage()).isEqualTo(1),
        () -> assertThat(result.getTotalItems()).isEqualTo(2),
        () -> assertThat(result.getTotalPages()).isEqualTo(1),
        () -> assertThat(result.getItems()).hasSize(2),
        () -> assertThat(result.isHasNext()).isFalse());
    verify(listingRepository, times(1))
        .findAllWithFilter(city, annualRent, apartmentType, pageable);
  }

  @Test
  @DisplayName("Zero listing result found")
  void givenValidQueryWhenUserRequestNonExistentDataThenReturnEmptyResult() {
    // Given
    int page = 0;
    int size = 20;
    String city = "Lagos";
    int annualRent = 200000;
    String apartmentType = "DUPLEX";
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    given(listingRepository.findAllWithFilter(city, annualRent, apartmentType, pageable))
        .willReturn(Page.empty());

    // When
    PaginatedResponse<Listing> response =
        listingService.getFilteredListings(page, size, city, annualRent, apartmentType);

    // Then
    assertAll(
        () -> assertThat(response).isNotNull(),
        () -> assertThat(response.getItems()).isEmpty(),
        () -> assertThat(response.getTotalItems()).isZero(),
        () -> assertThat(response.getTotalPages()).isOne(),
        () -> assertThat(response.isHasNext()).isFalse());
    verify(listingRepository, times(1))
        .findAllWithFilter(city, annualRent, apartmentType, pageable);
  }

  @Test
  @DisplayName("Invalid page or size values")
  void givenInvalidPageOrSizeValueWhenUserRequestThenThrowException() {
    // Given
    int page = -1;
    int size = 0;
    String city = "Lagos";
    int annualRent = 200000;
    String apartmentType = "DUPLEX";

    // When
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> listingService.getFilteredListings(page, size, city, annualRent, apartmentType));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Page index must not be less than zero");
  }

  @Test
  @DisplayName("Get listings by agentId")
  void givenValidPageAndSizeAndUserIdWhenUserRequestThenReturnResult() {
    // Given
    int page = 0;
    int size = 2;
    Integer userId = 1;
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Listing listing1 = new Listing();
    listing1.setId("2746bdfrhkjfdhfjdd");
    listing1.setApartmentInfo(new ApartmentInfo(3, 2, 2, ApartmentType.DUPLEX));
    listing1.setCost(new ListingCost(200000, 20000, 50000, 20000));

    Listing listing2 = new Listing();
    listing2.setId("kdjkfjieu8y48uu3t");
    listing2.setApartmentInfo(new ApartmentInfo(4, 2, 3, ApartmentType.DUPLEX));
    listing2.setCost(new ListingCost(350000, 35000, 50000, 35000));

    List<Listing> listings = List.of(listing1, listing2);
    Page<Listing> mockPage = new PageImpl<>(listings, PageRequest.of(page, size), 2);
    given(userService.extractUserId()).willReturn(userId);
    given(listingRepository.findListingsByAgentId(userId, pageable)).willReturn(mockPage);

    // When
    PaginatedResponse<Listing> result = listingService.getListingsByAgentId(page, size);

    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.getTotalItems()).isEqualTo(2),
        () -> assertThat(result.getCurrentPage()).isEqualTo(1),
        () -> assertThat(result.getTotalItems()).isEqualTo(2),
        () -> assertThat(result.getTotalPages()).isEqualTo(1),
        () -> assertThat(result.getItems()).hasSize(2),
        () -> assertThat(result.isHasNext()).isFalse());
    verify(listingRepository, times(1)).findListingsByAgentId(userId, pageable);
  }

  @Test
  @DisplayName("Empty listings")
  void givenValidUserIdWhenUserRequestButNoListingsYetThenReturnEmpty() {
    // Given
    int page = 0;
    int size = 2;
    Integer userId = 1;
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    Page<Listing> emptyPage = Page.empty(pageable);

    given(userService.extractUserId()).willReturn(userId);
    given(listingRepository.findListingsByAgentId(userId, pageable)).willReturn(emptyPage);

    // When
    PaginatedResponse<Listing> response = listingService.getListingsByAgentId(page, size);

    // Then
    assertAll(
        () -> assertThat(response.getItems()).isEmpty(),
        () -> assertThat(response.getTotalItems()).isEqualTo(0),
        () -> assertThat(response.getTotalPages()).isEqualTo(0),
        () -> assertThat(response.isHasNext()).isFalse());

    verify(listingRepository).findListingsByAgentId(userId, pageable);
  }

  @Test
  @DisplayName("Update rented status successfully by authorized agent")
  void givenValidListingAndAgentIdWhenUpdatingThenUpdateSuccessfully() {
    // Given
    RentUpdate rentUpdate = new RentUpdate("listing-id", true);
    Listing listing = new Listing();
    listing.setAgentId(1);
    given(listingRepository.findById("listing-id")).willReturn(Optional.of(listing));
    given(userService.extractUserId()).willReturn(1);
    given(listingRepository.save(listing)).willReturn(listing);

    // When
    Listing updatedListing = listingService.updateRentedListing(rentUpdate);

    // Then
    assertThat(updatedListing.isRented()).isTrue();
    assertThat(updatedListing.getRentDate()).isNotNull();
    verify(listingRepository).save(listing);
  }

  @Test
  @DisplayName("Reset rent date when setting rented status to false")
  void givenRentedListingWhenUnrentedThenResetRentDate() {
    // Given
    RentUpdate rentUpdate = new RentUpdate("listing-id", false);
    Listing listing = new Listing();
    listing.setAgentId(1);
    listing.setRented(true);
    listing.setRentDate(LocalDateTime.now());

    given(listingRepository.findById("listing-id")).willReturn(Optional.of(listing));
    given(userService.extractUserId()).willReturn(1);
    given(listingRepository.save(listing)).willReturn(listing);

    // When
    Listing updatedListing = listingService.updateRentedListing(rentUpdate);

    // Then
    assertThat(updatedListing.isRented()).isFalse();
    assertThat(updatedListing.getRentDate()).isNull();
    verify(listingRepository).save(listing);
  }

  @Test
  @DisplayName("Handle idempotent updates gracefully when status remains the same")
  void givenSameRentedStatusWhenUpdatingThenHandleGracefully() {
    // Given
    RentUpdate rentUpdate = new RentUpdate("listing-id", false);
    Listing listing = new Listing();
    listing.setAgentId(1);
    listing.setRented(false);

    given(listingRepository.findById("listing-id")).willReturn(Optional.of(listing));
    given(userService.extractUserId()).willReturn(1);
    given(listingRepository.save(listing)).willReturn(listing);

    // When
    Listing updatedListing = listingService.updateRentedListing(rentUpdate);

    // Then
    assertThat(updatedListing.isRented()).isFalse();
    verify(listingRepository).save(listing);
  }

  @Test
  @DisplayName("Throw NotFoundException when listing is not found")
  void givenInvalidListingIdWhenUpdatingThenThrowNotFoundException() {
    // Given
    RentUpdate rentUpdate = new RentUpdate("invalid-id", true);
    given(listingRepository.findById("invalid-id")).willReturn(Optional.empty());

    // When
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> listingService.updateRentedListing(rentUpdate));
    // Then
    assertThat(exception.getMessage()).isEqualTo("Listing not found");
  }

  @Test
  void updateRentedListing() {}
}
