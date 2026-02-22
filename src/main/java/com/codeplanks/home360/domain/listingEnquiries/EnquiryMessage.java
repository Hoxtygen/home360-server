/* (C)2025-2026 */
package com.codeplanks.home360.domain.listingEnquiries;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "enquiryMessages")
public class EnquiryMessage {
  @Id private String id;

  @Indexed
  @Field(name = "enquiryId", targetType = FieldType.STRING)
  private String enquiryId;

  @Field(name = "senderId", targetType = FieldType.INT32)
  private Integer senderId;

  @Field(name = "receiverId", targetType = FieldType.INT32)
  private Integer receiverId;

  @Field(name = "content", targetType = FieldType.STRING)
  private String content;

  @Field(name = "created_at", targetType = FieldType.DATE_TIME)
  private ZonedDateTime createdAt;
}
