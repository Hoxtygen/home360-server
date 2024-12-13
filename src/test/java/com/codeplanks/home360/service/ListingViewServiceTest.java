/* (C)2024 */
package com.codeplanks.home360.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codeplanks.home360.domain.listing.Listing;
import com.codeplanks.home360.domain.listingView.ListingView;
import com.codeplanks.home360.domain.listingView.ListingViewDTO;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.ListingRepository;
import com.codeplanks.home360.repository.ListingViewRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
class ListingViewServiceTest {
  @InjectMocks ListingViewServiceImpl listingViewService;

  @Mock ListingViewRepository listingViewRepository;

  @Mock ListingServiceImpl listingService;

  @Mock UserServiceImpl userService;

  @Mock ListingRepository listingRepository;

  @BeforeEach
  void setUp() {}

  @Test
  @DisplayName("save listing view")
  void givenValidListingViewRequestWhenSavedThenNewListingViewIsCreated() {
    // Given
    Listing listing = new Listing();
    listing.setId("663b268e5512f1692718c3ec");
    ListingViewDTO request =
        ListingViewDTO.builder()
            .listingId(listing.getId())
            .timestamp(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    ListingView listingView =
        ListingView.builder()
            .listingId(listing.getId())
            .timestamp(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    given(listingViewRepository.save(any(ListingView.class))).willReturn(listingView);
    // When
    ListingView result = listingViewService.saveListingView(request);
    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> {
          assert result != null;
          assertThat(result.getListingId()).isEqualTo("663b268e5512f1692718c3ec");
        },
        () -> assertThat(result.getTimestamp()).isNotNull(),
        () -> assertThat(result.getTimestamp()).isInstanceOf(LocalDateTime.class));

    verify(listingViewRepository, times(1)).save(any(ListingView.class));
  }

  @Test
  @DisplayName("Invalid listing Id")
  void givenListingViewRequestWithInvalidListingIdWhenSavedThenShouldThrowError() {
    // Given
    String listingId = "663b268e5512f1692718c3ec6e";
    Listing listing = new Listing();
    listing.setId(listingId);

    listing.setId(listingId);
    ListingViewDTO request =
        ListingViewDTO.builder()
            .listingId(listingId)
            .timestamp(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    given(listingService.findListingById(listingId))
        .willThrow(new NotFoundException("Listing not found"));
    // When
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> listingViewService.saveListingView(request));
    // Then
    assertEquals("Listing not found", exception.getMessage());
  }

  @Test
  @DisplayName("Get all listing views")
  void givenListingViewsExistWhenGetAllListingViewsThenReturnListOfListingViews() {
    // Given
    ListingView listingView1 = new ListingView();
    ListingView listingView2 = new ListingView();
    ListingView listingView3 = new ListingView();
    ListingView listingView4 = new ListingView();
    List<ListingView> listingViews =
        Arrays.asList(listingView1, listingView2, listingView3, listingView4);
    given(listingViewRepository.findAll()).willReturn(listingViews);
    // When
    List<ListingView> result = listingViewService.getAllListingViews();
    // Then
    assertNotNull(result);
    assertEquals(4, result.size());
    verify(listingViewRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Empty listing views")
  void givenNoListingViewsExistWhenAllListingViewsThenReturnEmptyList() {
    // Given
    given(listingViewRepository.findAll()).willReturn(Collections.emptyList());

    // When
    List<ListingView> result = listingViewService.getAllListingViews();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(listingViewRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Get listing views successfully")
  void givenAListingIdWhenRequestForItsViewsThenReturnResult() {
    // Given
    String listingId = "663b268e5512f1692718c3d3";
    Integer userId = 1;

    AppUser user = new AppUser();
    user.setId(userId);

    ListingView listingView1 = new ListingView();
    ListingView listingView2 = new ListingView();
    List<ListingView> listingViews = Arrays.asList(listingView1, listingView2);

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(userId);

    given(listingService.findListingById(listingId)).willReturn(listing);
    given(userService.extractUserId()).willReturn(userId);
    given(listingViewRepository.findByListingId(listingId)).willReturn(listingViews);

    // When
    List<ListingView> result = listingViewService.getViewsByListingId(listingId);
    // Then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    verify(userService, times(1)).extractUserId();
    verify(listingViewRepository, times(1)).findByListingId(listingId);
  }

  @Test
  @DisplayName("Invalid listingId")
  void givenInvalidListingIdWhenRequestForViewsThenThrowNotFoundException() {
    // Given
    String listingId = "78edio";
    Integer userId = 1;
    given(userService.extractUserId()).willReturn(userId);
    given(listingService.findListingById(listingId))
        .willThrow(new NotFoundException("Listing not found"));

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> {
              listingViewService.getViewsByListingId(listingId);
            });

    // Then
    assertEquals("Listing not found", exception.getMessage());
  }

  @Test
  @DisplayName("Empty listing views")
  void givenAListingIdWithNoViewsWhenRequestForItsViewsThenReturnEmptyResult() {
    // Given
    String listingId = "663b268e5512f1692718c3d3";
    Integer userId = 1;

    AppUser user = new AppUser();
    user.setId(userId);

    List<ListingView> listingViews = List.of();

    Listing listing = new Listing();
    listing.setId(listingId);
    listing.setAgentId(userId);

    given(listingService.findListingById(listingId)).willReturn(listing);
    given(userService.extractUserId()).willReturn(userId);
    given(listingViewRepository.findByListingId(listingId)).willReturn(listingViews);

    // When
    List<ListingView> result = listingViewService.getViewsByListingId(listingId);
    // Then
    assertThat(result).isEmpty();
    verify(userService, times(1)).extractUserId();
    verify(listingViewRepository, times(1)).findByListingId(listingId);
  }

  @Test
  @DisplayName("Invalid user access")
  void givenUserIdWhenRequestForViewsThenThrowAccessDeniedException() {
    // Given
    String listingId = "663b268e5512f1692718c3d3";
    Integer userId = 1;

    Listing listing = new Listing();
    listing.setAgentId(2);

    AppUser user = new AppUser();
    user.setId(userId);

    given(listingService.findListingById(listingId)).willReturn(listing);
    given(userService.extractUserId()).willReturn(userId);

    // When
    AccessDeniedException exception =
        assertThrows(
            AccessDeniedException.class,
            () -> {
              listingViewService.getViewsByListingId(listingId);
            });

    // Then
    assertEquals("You are forbidden from accessing this data", exception.getMessage());
    verify(listingViewRepository, times(0)).findByListingId(listingId);
    verify(userService, times(1)).extractUserId();
    verify(listingService, times(1)).findListingById(listingId);
  }
}
