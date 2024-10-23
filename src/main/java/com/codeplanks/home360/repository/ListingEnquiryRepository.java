/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ListingEnquiryRepository
    extends MongoRepository<ListingEnquiry, String>, FilterableRepository<ListingEnquiry> {}
