/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.listing.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Wasiu Idowu
 *
 * */
public interface ListingRepository
    extends MongoRepository<Listing, String>, FilterableRepository<Listing> {}
