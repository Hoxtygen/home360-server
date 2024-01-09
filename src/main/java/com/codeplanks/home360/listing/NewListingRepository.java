package com.codeplanks.home360.listing;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewListingRepository extends MongoRepository<NewListingEntity, String> {
}
