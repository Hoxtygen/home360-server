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
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.ListingRepository;
import com.codeplanks.home360.repository.ListingViewRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ListingViewServiceTest {
  @InjectMocks ListingViewServiceImpl listingViewService;

  @Mock ListingViewRepository listingViewRepository;

  @Mock ListingServiceImpl listingService;
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
}
