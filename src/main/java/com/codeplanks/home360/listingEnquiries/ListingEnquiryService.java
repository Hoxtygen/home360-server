package com.codeplanks.home360.listingEnquiries;

import com.codeplanks.home360.auth.AuthenticationServiceImpl;
import com.codeplanks.home360.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ListingEnquiryService implements EnquiryServiceRepository {
  @Autowired
  private final AuthenticationServiceImpl authenticationService;
  private final ListingEnquiryRepository listingEnquiryRepository;

  @Override
  public ListingEnquiryDTO makeEnquiry(ListingEnquiry enquiryRequest) {
    if (AuthenticationUtils.isAuthenticated()){
      Integer userId = authenticationService.extractUserId();
      enquiryRequest.setUserId(userId);
    }

    enquiryRequest.setCreatedAt(LocalDateTime.now());
    ListingEnquiry listingEnquiry = listingEnquiryRepository.save(enquiryRequest);
    return ListingEnquiryMapper.mapToListingEnquiryDTO(listingEnquiry);
  }
}
