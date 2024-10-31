/* (C)2024 */
package com.codeplanks.home360.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomListingEnquiryRepositoryTest {
  @Mock private MongoTemplate mongoTemplate;
  @InjectMocks private CustomListingEnquiryRepositoryImpl customListingEnquiryRepository;

  private Pageable pageable;
  private Query expectedQuery;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void findListingEnquiries() {
    // Given

    // When

    // Then

  }
}
