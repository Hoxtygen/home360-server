package com.codeplanks.home360.domain.listingEnquiries;

import com.codeplanks.home360.validation.ValidEmail;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "listingEnquiries")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingEnquiry {
  @Id
  private String id;

  @Field(name = "firstName", targetType = FieldType.STRING)
  @NotBlank(message = "First name is required")
  @Size(min = 3, max = 50, message = "First name should be between 3-50 characters")
  private String firstName;

  @Field(name = "lastName", targetType = FieldType.STRING)
  @NotBlank(message = "Last name is required")
  @Size(min = 3, max = 50, message = "Last name should be between 3-50 characters")
  private String lastName;

  @Field(name = "email", targetType = FieldType.STRING)
  @NotBlank(message = "Email is required")
  @Email(message = "Enter a valid email address")
  @ValidEmail(message = "Enter a valid email address")
  private String email;

  @Field(name = "phoneNumber")
  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^([0]{1})([7-9]{1})([0|1]{1})([\\d]{1})([\\d]{7,8})$", message =
          "Enter a valid Nigerian number E.g 08022345678")
  @Size(min = 11, max = 11, message = "Phone number must be 11 characters long.")
  private String phoneNumber;


  @Field(name = "location", targetType = FieldType.STRING)
  @NotBlank(message = "Location is required")
  @Size(min = 3, max = 50, message = "Location should be between 3-50 characters")
  private String location;

  @Field(name = "salutation", targetType = FieldType.STRING)
  @NotBlank(message = "Salutation is required")
  private String salutation;

  @Field(name = "message", targetType = FieldType.STRING)
  @NotBlank(message = "Message is required")
  @Size(min = 3, max = 2000, message = "message should be between 3-2000 characters")
  private String message;

  @Field(name = "employmentStatus")
  @NotNull(message = "Employment status is required")
  @Valid
  private EmploymentStatus employmentStatus;

  @Field(name = "pets", targetType = FieldType.STRING)
  @NotBlank(message = "Pets is required")
  private String pets;

  @Field(name = "commercialPurpose", targetType = FieldType.STRING)
  @NotBlank(message = "Commercial purpose is required")
  private String commercialPurpose;

  @NotBlank(message = "ListingId field is required")
  @Field(name = "listingId", targetType = FieldType.STRING)
  private String listingId;

  @Field(name = "userId", targetType = FieldType.INT32)
  private Integer userId;

  @Field(name = "agentId", targetType = FieldType.INT32)
  @NotNull(message = "Agent Id is required")
  private Integer agentId;

  @Field(name = "created_at", targetType = FieldType.DATE_TIME)
  private LocalDateTime createdAt;

  @Field(name = "read", targetType = FieldType.BOOLEAN)
  private boolean read = false;
}
