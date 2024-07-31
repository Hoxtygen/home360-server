package com.codeplanks.home360.listingEnquiries;

import com.codeplanks.home360.utils.FilterableRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ListingEnquiryRepository extends MongoRepository<ListingEnquiry, String>,
        FilterableRepository<ListingEnquiry> {
  Optional<ListingEnquiryDTO> findByAgentId(Integer agentId);

}
