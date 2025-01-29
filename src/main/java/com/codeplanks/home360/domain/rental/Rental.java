/* (C)2025 */
package com.codeplanks.home360.domain.rental;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "rentals")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class Rental {
  @Id private String id;

  @Field(name = "renterId", targetType = FieldType.INT32)
  private Integer renterId;

  @Field(name = "rentDuration", targetType = FieldType.STRING)
  private String rentDuration;

  @Field(name = "rentStartDate", targetType = FieldType.DATE_TIME)
  private LocalDateTime rentStartDate;

  @Field(name = "rentDueDate", targetType = FieldType.DATE_TIME)
  private LocalDateTime rentDueDate;

  @Field(name = "created_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime createdAt;

  @Field(name = "modified_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime modifiedAt;

  @Field(name = "listingId", targetType = FieldType.STRING)
  private String listingId;

  @Field(name = "agentId", targetType = FieldType.INT32)
  private Integer agentId;
}
