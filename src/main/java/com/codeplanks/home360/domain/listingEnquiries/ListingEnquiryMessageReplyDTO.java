package com.codeplanks.home360.domain.listingEnquiries;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListingEnquiryMessageReplyDTO {
  @NotNull(message = "sender is required")
  private  int sender;

  @NotNull(message = "receiver is required")
  private int receiver;

  @NotNull(message = "message content is required")
  @Size(min = 2,  message = "message should be at least 2 characters")
  private  String content;

}
