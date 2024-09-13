/* (C)2024 */
package com.codeplanks.home360.domain.listingEnquiries;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingEnquiryMessageReply {
  private String id;
  private int senderId;
  private int receiverId;
  private String content;
  private LocalDateTime createdAt;
}
