/* (C)2025-2026 */
package com.codeplanks.home360.domain.listingEnquiries;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingEnquiryMessageReplyDTO {
  @NotNull(message = "Agent ID is required")
  private Integer agentId;

  @NotNull(message = "Enquirer ID is required")
  private Integer enquirerId;

  @NotNull(message = "message content is required")
  @Size(min = 2, message = "message should be at least 2 characters")
  private String content;

  @NotNull(message = "Enquiry ID is required")
  private String enquiryId;

  private Integer senderId;
}
