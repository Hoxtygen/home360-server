/* (C)2025 */
package com.codeplanks.home360.domain.listingEnquiries;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListingEnquiryMessageReplyDTO {
  @NotNull(message = "Agent ID is required")
  private int agentId;

  @NotNull(message = "Enquirer ID is required")
  private int enquirerId;

  @NotNull(message = "message content is required")
  @Size(min = 2, message = "message should be at least 2 characters")
  private String content;

  @NotNull(message = "Enquiry ID is required")
  private String enquiryId;

  private int senderId;
}
