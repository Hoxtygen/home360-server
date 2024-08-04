package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ListingEnquiryRepository extends MongoRepository<ListingEnquiry, String>,
        FilterableRepository<ListingEnquiry> {
  Optional<ListingEnquiryDTO> findByAgentId(Integer agentId);

}
