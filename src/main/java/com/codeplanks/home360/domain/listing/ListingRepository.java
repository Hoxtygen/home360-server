package com.codeplanks.home360.domain.listing;

import com.codeplanks.home360.repository.FilterableRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


/**
 * @author Wasiu Idowu
 *
 * */

public interface ListingRepository extends MongoRepository<Listing, String>,
        FilterableRepository<Listing> {
  Optional<ListingDTO> findByAgentId(Integer agentId);

}