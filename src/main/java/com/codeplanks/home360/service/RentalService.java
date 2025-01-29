/* (C)2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.rental.Rental;
import com.codeplanks.home360.domain.rental.RentalDTO;

public interface RentalService {
  Rental saveRentDetails(RentalDTO rentalRequest);
}
