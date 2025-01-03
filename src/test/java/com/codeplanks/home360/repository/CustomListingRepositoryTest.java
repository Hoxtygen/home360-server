/* (C)2024-2025 */
package com.codeplanks.home360.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.codeplanks.home360.domain.listing.Listing;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomListingRepositoryTest {
  @InjectMocks private CustomListingRepositoryImpl customListingRepository;

  @Mock private MongoTemplate mongoTemplate;

  @Mock private Pageable pageable;

  @Test
  void findAllWithFilter() {
    // Given
    String city = "New York";
    int annualRent = 1000;
    String apartmentType = "Studio";
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created_at"));
    List<Listing> listings = List.of(new Listing());

    given(mongoTemplate.find(any(Query.class), eq(Listing.class), eq("listings")))
        .willReturn(listings);

    // When
    Page<Listing> result =
        customListingRepository.findAllWithFilter(city, annualRent, apartmentType, pageable);

    // Then
    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getTotalElements()).isEqualTo(1);

    // Capture the query passed to mongoTemplate.find
    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(Listing.class), eq("listings"));

    Query capturedQuery = queryCaptor.getValue();

    assertThat(capturedQuery.getQueryObject().get("address.city")).isEqualTo(city);
    assertThat(capturedQuery.getQueryObject().get("cost.annualRent"))
        .isEqualTo(new Criteria().gte(annualRent).getCriteriaObject());
    assertThat(capturedQuery.getQueryObject().get("apartmentInfo.apartmentType"))
        .isEqualTo(apartmentType);

    Document sortDocument = capturedQuery.getSortObject();
    assertThat(sortDocument).isNotNull();
    assertThat(sortDocument.get("created_at")).isEqualTo(-1);
  }

  @Test
  void findListingsByAgentId() {
    // Given
    Integer agentId = 1;
    List<Listing> listings = List.of(new Listing());
    pageable = PageRequest.of(0, 10).withSort(Sort.Direction.DESC, "created_at");

    given(mongoTemplate.find(any(Query.class), eq(Listing.class), eq("listings")))
        .willReturn(listings);

    // When
    Page<Listing> result = customListingRepository.findListingsByAgentId(agentId, pageable);

    // Then
    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getTotalElements()).isEqualTo(1);

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(Listing.class), eq("listings"));

    Query capturedQuery = queryCaptor.getValue();

    assertThat(capturedQuery.getQueryObject().get("agentId")).isEqualTo(agentId);
    assertThat(capturedQuery.getSortObject().get("created_at")).isEqualTo(-1);
  }

  @Test
  void findListingWithViewCountById() {
    // Given

    // When

    // Then
  }
}
