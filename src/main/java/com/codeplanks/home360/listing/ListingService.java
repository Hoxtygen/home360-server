package com.codeplanks.home360.listing;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.exception.UserNotFoundException;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ListingService implements ListingServiceRepository {
  @Autowired
  private final ListingRepository listingRepository;
  private final UserRepository userRepository;
  private final HttpServletRequest httpServletRequest;
  private final JwtService jwtService;

  Logger logger = LoggerFactory.getLogger(ListingService.class);

  public ListingDTO createListing(Listing request) {
    String authHeader = httpServletRequest.getHeader("Authorization");
    String token = authHeader.substring(7);

    String username = jwtService.extractUsername(token);
    Integer userId = getUser(username).getId();

    request.setAgentId(userId);
    request.setCreatedAt(LocalDateTime.now());
    request.setUpdatedAt(LocalDateTime.now());
    Listing savedListing = listingRepository.save(request);

    logger.info("Listing created successfully: " + savedListing);

    return ListingMapper.mapToListingDTO(savedListing);

  }

  public List<Listing> allListings() {
    return listingRepository.findAll();
  }

  private AppUser getUser(String email) throws UserNotFoundException {
    return userRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException("Agent does not exist"));
  }

}
