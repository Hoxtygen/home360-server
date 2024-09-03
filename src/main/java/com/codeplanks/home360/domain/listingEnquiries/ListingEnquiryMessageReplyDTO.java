package com.codeplanks.home360.domain.listingEnquiries;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListingEnquiryMessageReplyDTO {
  @NotNull(message = "sender ID is required")
  private  int senderId;

  @NotNull(message = "receiver ID is required")
  private int receiverId;

  @NotNull(message = "message content is required")
  @Size(min = 2,  message = "message should be at least 2 characters")
  private  String content;

}
