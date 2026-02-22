/* (C)2025 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listingEnquiries.EnquiryMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnquiryMessageRepository extends MongoRepository<EnquiryMessage, String> {
  Page<EnquiryMessage> findByEnquiryId(String enquiryId, Pageable pageable);
}
