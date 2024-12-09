/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listingView.ListingView;
import com.codeplanks.home360.domain.listingView.ListingViewDTO;
import java.util.List;

public interface ListingViewService {
  ListingView saveListingView(ListingViewDTO request);

  List<ListingView> getAllListingViews();
}
