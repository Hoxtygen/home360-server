/* (C)2025 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.rental.Rental;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RentalRepository extends MongoRepository<Rental, String> {}
