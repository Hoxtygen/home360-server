/* (C)2024 */
package com.codeplanks.home360.domain.listingEnquiries;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "listingEnquiries")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@Schema(description = "Listing Enquiry Model Information")
public class ListingEnquiry {
  @Id private String id;

  @Field(name = "firstName", targetType = FieldType.STRING)
  private String firstName;

  @Field(name = "lastName", targetType = FieldType.STRING)
  private String lastName;

  @Field(name = "email", targetType = FieldType.STRING)
  private String email;

  @Field(name = "phoneNumber")
  private String phoneNumber;

  @Field(name = "location", targetType = FieldType.STRING)
  private String location;

  @Field(name = "salutation", targetType = FieldType.STRING)
  private String salutation;

  @Field(name = "message", targetType = FieldType.STRING)
  private String message;

  @Field(name = "employmentStatus")
  private EmploymentStatus employmentStatus;

  @Field(name = "pets", targetType = FieldType.STRING)
  private String pets;

  @Field(name = "commercialPurpose", targetType = FieldType.STRING)
  private String commercialPurpose;

  @Field(name = "listingId", targetType = FieldType.STRING)
  private String listingId;

  @Field(name = "userId", targetType = FieldType.INT32)
  private Integer userId;

  @Field(name = "agentId", targetType = FieldType.INT32)
  private Integer agentId;

  @Field(name = "created_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime createdAt;

  @Field(name = "read", targetType = FieldType.BOOLEAN)
  private boolean read = false;

  @Field(name = "replies")
  private List<ListingEnquiryMessageReply> replies = new ArrayList<>();
}
