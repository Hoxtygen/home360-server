package com.codeplanks.home360.listing;

import com.codeplanks.home360.utils.FilterableRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


/**
 * @author Wasiu Idowu
 *
 * */

public interface ListingRepository extends MongoRepository<Listing, String>,
        FilterableRepository<Listing> {
  Optional<ListingDTO> findByAgentId(Integer agentId);

}
