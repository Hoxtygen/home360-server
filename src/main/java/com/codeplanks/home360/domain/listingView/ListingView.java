/* (C)2024-2025 */
package com.codeplanks.home360.domain.listingView;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@Document(collection = "listingViews")
public class ListingView {
  @Id private String id;

  @Field(name = "listingId", targetType = FieldType.STRING)
  private String listingId;

  @Field(name = "timestamp", targetType = FieldType.DATE_TIME)
  private LocalDateTime timestamp;

  @Field(name = "created_at", targetType = FieldType.DATE_TIME)
  @CreatedDate
  private LocalDateTime createdAt;
}
