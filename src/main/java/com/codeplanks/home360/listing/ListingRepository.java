package com.codeplanks.home360.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ListingRepository extends MongoRepository<Listing, String> {
  Optional<ListingDTO> findByAgentId(Integer agentId);
}