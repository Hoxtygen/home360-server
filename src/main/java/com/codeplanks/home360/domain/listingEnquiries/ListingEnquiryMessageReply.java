package com.codeplanks.home360.domain.listingEnquiries;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingEnquiryMessageReply {
  @Id
  private String id;
  private  int senderId;
  private int receiverId;
  private  String content;
  private LocalDateTime createdAt;
}
