/* (C)2024 */
package com.codeplanks.home360.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.*;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.ListingEnquiryRepository;
import com.codeplanks.home360.repository.ListingRepository;
import com.codeplanks.home360.utils.AuthenticationUtils;
import com.mongodb.client.result.UpdateResult;
import jakarta.validation.*;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ListingEnquiryServiceTest {
  @InjectMocks ListingEnquiryServiceImpl listingEnquiryService;
  @Mock ListingRepository listingRepository;
  @Mock AuthenticationUtils authenticationUtils;
  @Mock ListingServiceImpl listingService;
  @Mock private ListingEnquiryRepository listingEnquiryRepository;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private UserServiceImpl userService;
  private ListingEnquiryDTO listingEnquiryDTO;
  private AppUser john;
  private AppUser jane;

  @BeforeEach
  void setUp() {
    listingEnquiryDTO =
        ListingEnquiryDTO.builder()
            .firstName("Machurian")
            .lastName("Lord")
            .email("Machurian_lord@gmail.com")
            .phoneNumber("09087937645")
            .location("London")
            .message("Lorem ipsum dolor sit amet consectetur adipscing")
            .salutation("Mr")
            .employmentStatus(EmploymentStatus.EMPLOYEE)
            .pets("YES")
            .commercialPurpose("NO")
            .listingId("66ee668716a31f4e7fbc9e42")
            .agentId(1)
            .createdAt(LocalDateTime.now())
            .build();

    john =
        new AppUser(
            1,
            "John",
            "Doe",
            "john_doe@example.com",
            "1245678",
            "221B Baker street",
            "080212345678",
            new Date(),
            new Date(),
            Role.USER,
            true);
    jane =
        new AppUser(
            2,
            "Jane",
            "Doe",
            "jane_doe@example.com",
            "1245678",
            "221C Butler street",
            "080212345687",
            new Date(),
            new Date(),
            Role.USER,
            true);
  }

  @Test
  @DisplayName("Make enquiry successfully")
  void givenValidListingEnquiryObjectWhenUserMakeEnquiryThenRequestSaved() {
    // Given
    ListingEnquiry savedEnquiry =
        ListingEnquiry.builder()
            .firstName("Machurian")
            .lastName("Lord")
            .email("Machurian_lord@gmail.com")
            .phoneNumber("09087937645")
            .location("London")
            .listingId("66ee668716a31f4e7fbc9e42")
            .agentId(1)
            .createdAt(LocalDateTime.now())
            .build();
    given(authenticationUtils.isAuthenticated()).willReturn(false);
    given(listingEnquiryRepository.save(any(ListingEnquiry.class))).willReturn(savedEnquiry);

    // When
    ListingEnquiry result = listingEnquiryService.makeEnquiry(listingEnquiryDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("Machurian_lord@gmail.com");
  }

  @Test
  @DisplayName("Null email")
  void givenRequestWithoutEmailWhenUserMakeEnquiryThenThrowViolation() {
    // Given
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    listingEnquiryDTO.setEmail(null);

    // When
    Set<ConstraintViolation<ListingEnquiryDTO>> violations = validator.validate(listingEnquiryDTO);

    // Then
    assertThat(violations).isNotNull();
    assertThat(
            violations.stream().anyMatch(value -> value.getMessage().equals("Email is required")))
        .isTrue();
    verify(listingEnquiryRepository, never()).save(any(ListingEnquiry.class));
  }

  @Test
  @DisplayName("Null multiple required fields")
  void givenRequestWithoutMultipleRequiredFieldsWhenUserMakeEnquiryThenThrowViolation() {
    // Given
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    listingEnquiryDTO.setEmail(null);
    listingEnquiryDTO.setLastName(null);
    listingEnquiryDTO.setPhoneNumber(null);
    listingEnquiryDTO.setAgentId(null);

    // When
    Set<ConstraintViolation<ListingEnquiryDTO>> violations = validator.validate(listingEnquiryDTO);

    // Then
    List<String> messages = violations.stream().map(ConstraintViolation::getMessage).toList();

    assertAll(
        () -> assertThat(violations).isNotEmpty(),
        () ->
            assertThat(messages)
                .contains(
                    "Last name is required",
                    "Email is required",
                    "Phone number is required",
                    "Agent Id is required"),
        () -> assertThat(messages).doesNotContain("First name is required"));

    verify(listingEnquiryRepository, never()).save(any(ListingEnquiry.class));
  }

  @Test
  @DisplayName("Wrong phone number format")
  void givenRequestWrongPhoneNumberFormatWhenUserMakeEnquiryThenThrowViolation() {
    // Given
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    listingEnquiryDTO.setPhoneNumber("08823908437");

    // When
    Set<ConstraintViolation<ListingEnquiryDTO>> violations = validator.validate(listingEnquiryDTO);

    // Then
    assertThat(violations).isNotNull();
    assertThat(
            violations.stream()
                .anyMatch(
                    value ->
                        value.getMessage().equals("Enter a valid Nigerian number E.g 08022345678")))
        .isTrue();
    verify(listingEnquiryRepository, never()).save(any(ListingEnquiry.class));
  }

  @Test
  @DisplayName("Get user listing enquiries")
  void givenValidAgentIdWhenUserRequestForListingEnquiriesThenUserGetData() {
    // Given
    int page = 0;
    int size = 5;
    Integer senderId = 1;
    Integer agentId = 1;
    ListingEnquiry listingEnquiry1 =
        ListingEnquiry.builder()
            .id("64bd652852212f03ee0d2158")
            .firstName("Manchurian")
            .lastName("Lord")
            .email("Machurian_lord@gmail.com")
            .phoneNumber("09087937645")
            .location("London")
            .listingId("66ee668716a31f4e7fbc9e42")
            .agentId(1)
            .createdAt(LocalDateTime.now())
            .build();
    ListingEnquiry listingEnquiry2 =
        ListingEnquiry.builder()
            .id("64bd6bcc52212f03ee0d2159")
            .firstName("Joe")
            .lastName("Allen")
            .email("joe_allen@example.com")
            .phoneNumber("09087973645")
            .location("London")
            .listingId("66ee668716a31f4e7fbc9e42")
            .agentId(1)
            .createdAt(LocalDateTime.now())
            .build();

    List<ListingEnquiry> enquiries = List.of(listingEnquiry1, listingEnquiry2);
    Page<ListingEnquiry> mockPage = new PageImpl<>(enquiries, PageRequest.of(page, size), 2);
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    given(userService.extractUserId()).willReturn(agentId);
    given(
            listingEnquiryRepository.findListingEnquiries(
                ListingEnquiry.class, agentId, senderId, pageable))
        .willReturn(mockPage);
    given(
            listingEnquiryRepository.findListingEnquiries(
                ListingEnquiry.class, agentId, senderId, pageable))
        .willReturn(mockPage);

    // When
    PaginatedResponse<ListingEnquiry> result = listingEnquiryService.getListingEnquiries(0, 5, 1);

    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.getTotalItems()).isEqualTo(2),
        () -> assertThat(result.getCurrentPage()).isEqualTo(1),
        () -> assertThat(result.getTotalItems()).isEqualTo(2),
        () -> assertThat(result.getTotalPages()).isEqualTo(1),
        () -> assertThat(result.getItems()).hasSize(2),
        () -> assertThat(result.isHasNext()).isFalse());

    verify(listingEnquiryRepository, times(1))
        .findListingEnquiries(ListingEnquiry.class, agentId, senderId, pageable);
  }

  @Test
  @DisplayName("Failed user ID extraction")
  void givenInvalidUserIdWhenUserFetchDataThenThrowException() {
    // Given
    given(userService.extractUserId()).willThrow(new RuntimeException("User not authenticated"));

    // When
    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> listingEnquiryService.getListingEnquiries(0, 5, null));

    // Then
    assertThat(exception.getMessage()).isEqualTo("User not authenticated");
    verify(listingEnquiryRepository, never()).findListingEnquiries(any(), anyInt(), any(), any());
  }

  @Test
  @DisplayName("Zero listing enquiries found")
  void givenNoListingEnquiryWhenUserRequestForEnquiriesThenReturnEmptyData() {
    // Given
    int page = 0, size = 5;
    Integer agentId = 1;
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");
    given(userService.extractUserId()).willReturn(agentId);
    given(
            listingEnquiryRepository.findListingEnquiries(
                ListingEnquiry.class, agentId, null, pageable))
        .willReturn(Page.empty());

    // When
    PaginatedResponse<ListingEnquiry> response =
        listingEnquiryService.getListingEnquiries(page, size, null);

    // Then
    assertAll(
        () -> assertThat(response).isNotNull(),
        () -> assertThat(response.getItems()).isEmpty(),
        () -> assertThat(response.getTotalItems()).isZero(),
        () -> assertThat(response.getTotalPages()).isOne(),
        () -> assertThat(response.isHasNext()).isFalse());
  }

  @Test
  @DisplayName("Invalid page or size values")
  void givenInvalidPageOrSizeValueWhenUserRequestThenThrowException() {
    // Given
    int page = -1, size = 0;

    // When
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> listingEnquiryService.getListingEnquiries(page, size, null));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Page index must not be less than zero");
    verify(listingEnquiryRepository, never()).findListingEnquiries(any(), anyInt(), any(), any());
  }

  @Test
  @DisplayName("Database failure")
  void givenErrorWithDatabaseWhenUserRequestThenThrowException() {
    // Given
    int page = 0, size = 5;
    Integer agentId = 1;

    given(userService.extractUserId()).willReturn(agentId);
    given(listingEnquiryRepository.findListingEnquiries(any(), anyInt(), any(), any()))
        .willThrow(new RuntimeException("Database error"));

    // When
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> listingEnquiryService.getListingEnquiries(page, size, null));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Database error");
  }

  @Test
  @DisplayName("Invalid sender ID")
  void givenInvalidSenderIdWhenUserRequestThenThrowException() {
    // Given
    int page = 0, size = 5;
    Integer agentId = 1;
    Integer invalidSenderId = -999;
    Pageable pageable = PageRequest.of(page, size).withSort(Sort.Direction.DESC, "created_at");

    given(userService.extractUserId()).willReturn(agentId);
    given(
            listingEnquiryRepository.findListingEnquiries(
                ListingEnquiry.class, agentId, invalidSenderId, pageable))
        .willReturn(Page.empty());

    // When
    PaginatedResponse<ListingEnquiry> response =
        listingEnquiryService.getListingEnquiries(page, size, invalidSenderId);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getItems()).isEmpty();
  }

  @Test
  @DisplayName("Get listing enquiry by id successfully")
  void givenValidEnquiryIdWhenUserRequestThenReturnData() {
    // Given
    Integer agentId = 1;
    String listingEnquiryId = "64bd652852212f03ee0d2158";
    ListingEnquiry listingEnquiry =
        ListingEnquiry.builder()
            .id(listingEnquiryId)
            .firstName("Manchurian")
            .lastName("Lord")
            .email("Manchurian_lord@gmail.com")
            .phoneNumber("09087937645")
            .location("London")
            .listingId("66ee668716a31f4e7fbc9e42")
            .agentId(1)
            .createdAt(LocalDateTime.now())
            .build();

    given(userService.extractUserId()).willReturn(agentId);
    given(listingEnquiryRepository.findById(listingEnquiryId))
        .willReturn(Optional.ofNullable(listingEnquiry));

    // When
    ListingEnquiry response = listingEnquiryService.getListingEnquiryById(listingEnquiryId);

    // Then
    assertAll(
        () -> assertThat(response).isNotNull(),
        () -> assertThat(response.getEmail()).isEqualTo("Manchurian_lord@gmail.com"),
        () -> assertThat(response.getListingId()).isEqualTo("66ee668716a31f4e7fbc9e42"),
        () -> assertThat(response.getId()).isEqualTo(listingEnquiryId));
  }

  @Test
  @DisplayName("Invalid listing enquiry id")
  void givenInvalidListingEnquiryIdWhenUserRequestThenThrowException() {
    // Given
    String listingEnquiryId = "64bd652852212f03ee0d2158";

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> listingEnquiryService.getListingEnquiryById(listingEnquiryId));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Listing enquiry not found");
    verify(userService, never()).extractUserId();
    verify(listingEnquiryRepository, times(1)).findById(listingEnquiryId);
  }

  @Test
  @DisplayName("Invalid agent id")
  void givenInvalidAgentIdWhenUserRequestThenThrowException() {
    // Given
    Integer agentId = 1;
    Integer unauthorizedUserId = 5;
    String listingEnquiryId = "64bd652852212f03ee0d2158";
    ListingEnquiry listingEnquiry =
        ListingEnquiry.builder()
            .id(listingEnquiryId)
            .firstName("Manchurian")
            .lastName("Lord")
            .email("Manchurian_lord@gmail.com")
            .phoneNumber("09087937645")
            .location("London")
            .listingId("66ee668716a31f4e7fbc9e42")
            .agentId(agentId)
            .createdAt(LocalDateTime.now())
            .build();

    given(listingEnquiryRepository.findById(listingEnquiryId))
        .willReturn(Optional.of(listingEnquiry));
    given(userService.extractUserId()).willReturn(unauthorizedUserId);

    // When
    AccessDeniedException exception =
        assertThrows(
            AccessDeniedException.class,
            () -> listingEnquiryService.getListingEnquiryById(listingEnquiryId));

    // Then
    assertThat(exception.getMessage())
        .isEqualTo("Forbidden. You're not authorized to access this" + " data");
    verify(listingEnquiryRepository, times(1)).findById(listingEnquiryId);
  }

  @Test
  @DisplayName("Empty or null ID ")
  void givenNullOrEmptyEnquiryIdWhenGetListingEnquiryByIdThenThrowIllegalArgumentException() {
    // When
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> listingEnquiryService.getListingEnquiryById(null));
    // Then
    assertThat(exception.getMessage()).isEqualTo("Enquiry ID cannot be null or empty");
  }

  @Test
  @DisplayName("Empty or null agentId or userId")
  void givenEnquiryWithoutAgentOrUserIdWhenGetListingEnquiryByIdThenThrowAccessDeniedException() {
    // Given
    String listingEnquiryId = "64bd652852212f03ee0d2158";
    ListingEnquiry listingEnquiry =
        ListingEnquiry.builder().id(listingEnquiryId).agentId(null).userId(null).build();

    given(listingEnquiryRepository.findById(listingEnquiryId))
        .willReturn(Optional.of(listingEnquiry));
    given(userService.extractUserId()).willReturn(1);

    // When
    AccessDeniedException exception =
        assertThrows(
            AccessDeniedException.class,
            () -> listingEnquiryService.getListingEnquiryById(listingEnquiryId));

    // Then
    assertThat(exception.getMessage())
        .isEqualTo("Forbidden. You're not authorized to access this data");
  }

  @Test
  @DisplayName("User ID extraction fails")
  void givenUserIdExtractionFailureWhenGetListingEnquiryByIdThenThrowIllegalStateException() {
    // Given
    String listingEnquiryId = "64bd652852212f03ee0d2158";
    ListingEnquiry listingEnquiry =
        ListingEnquiry.builder().id(listingEnquiryId).agentId(1).build();

    given(listingEnquiryRepository.findById(listingEnquiryId))
        .willReturn(Optional.of(listingEnquiry));
    given(userService.extractUserId())
        .willThrow(new IllegalStateException("User ID extraction failed"));

    // When
    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> listingEnquiryService.getListingEnquiryById(listingEnquiryId));

    // Then
    assertThat(exception.getMessage()).isEqualTo("User ID extraction failed");
  }

  @Test
  @DisplayName("Database error")
  void givenDatabaseErrorWhenGetListingEnquiryByIdThenThrowRuntimeException() {
    // Given
    String listingEnquiryId = "64bd652852212f03ee0d2158";
    given(listingEnquiryRepository.findById(listingEnquiryId))
        .willThrow(new RuntimeException("Database error"));

    // When
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> listingEnquiryService.getListingEnquiryById(listingEnquiryId));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Database error");
  }

  @Test
  @DisplayName("Mark message as read successfully")
  void givenValidEnquiryIdWhenMarkMessageAsReadThenReturnTrue() {
    // Given
    String enquiryMessageId = "64bd652852212f03ee0d2158";
    ListingEnquiry listingEnquiry =
        ListingEnquiry.builder()
            .id(enquiryMessageId)
            .agentId(1) // Matching the user ID for authorization
            .read(false)
            .build();

    Query query = new Query(Criteria.where("_id").is(enquiryMessageId));

    given(mongoTemplate.exists(query, ListingEnquiry.class)).willReturn(true);
    given(mongoTemplate.findOne(query, ListingEnquiry.class)).willReturn(listingEnquiry);
    given(userService.extractUserId()).willReturn(1);

    UpdateResult updateResult = mock(UpdateResult.class);
    given(updateResult.getModifiedCount()).willReturn(1L);
    given(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(ListingEnquiry.class)))
        .willReturn(updateResult);

    // When
    Boolean result = listingEnquiryService.markMessageAsRead(enquiryMessageId);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Enquiry ID does not exist")
  void givenInvalidEnquiryIdWhenMarkMessageAsReadThenThrowNotFoundException() {
    // Given
    String enquiryMessageId = "invalid_id";
    Query query = new Query(Criteria.where("_id").is(enquiryMessageId));
    given(mongoTemplate.exists(query, ListingEnquiry.class)).willReturn(false);

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> listingEnquiryService.markMessageAsRead(enquiryMessageId));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Listing enquiry with the given ID was not found");
  }

  @Test
  @DisplayName("AccessDeniedException when user is not authorized")
  void givenUnauthorizedUserWhenMarkMessageAsReadThenThrowAccessDeniedException() {
    // Given
    String enquiryMessageId = "64bd652852212f03ee0d2158";
    ListingEnquiry listingEnquiry =
        ListingEnquiry.builder()
            .id(enquiryMessageId)
            .agentId(2) // Different agent ID
            .build();

    Query query = new Query(Criteria.where("_id").is(enquiryMessageId));
    given(mongoTemplate.exists(query, ListingEnquiry.class)).willReturn(true);
    given(mongoTemplate.findOne(query, ListingEnquiry.class)).willReturn(listingEnquiry);
    given(userService.extractUserId()).willReturn(1); // User ID does not match agent ID

    // When
    AccessDeniedException exception =
        assertThrows(
            AccessDeniedException.class,
            () -> listingEnquiryService.markMessageAsRead(enquiryMessageId));

    // Then
    assertThat(exception.getMessage())
        .isEqualTo("Forbidden. You're not authorized to access this data");
  }

  @Test
  @DisplayName("Return false if message was not updated")
  void givenValidEnquiryId_whenMarkMessageAsReadButNotModified_thenReturnFalse() {
    // Given
    String enquiryMessageId = "64bd652852212f03ee0d2158";
    ListingEnquiry listingEnquiry =
        ListingEnquiry.builder().id(enquiryMessageId).agentId(1).read(false).build();

    Query query = new Query(Criteria.where("_id").is(enquiryMessageId));

    given(mongoTemplate.exists(query, ListingEnquiry.class)).willReturn(true);
    given(mongoTemplate.findOne(query, ListingEnquiry.class)).willReturn(listingEnquiry);
    given(userService.extractUserId()).willReturn(1);

    UpdateResult updateResult = mock(UpdateResult.class);
    given(updateResult.getModifiedCount()).willReturn(0L);
    given(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(ListingEnquiry.class)))
        .willReturn(updateResult);

    // When
    Boolean result = listingEnquiryService.markMessageAsRead(enquiryMessageId);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Add reply message successfully")
  void givenValidInputWhenAddReplyMessageThenSuccess() {
    // Given
    String enquiryMessageId = "64bd652852212f03ee0d2158";
    ListingEnquiryMessageReplyDTO replyDTO = new ListingEnquiryMessageReplyDTO(1, 2, "Hello");
    Query query = new Query(Criteria.where("_id").is(enquiryMessageId));
    // Mock the dependencies
    given(userService.getUserByUserId(1)).willReturn(john);
    given(userService.getUserByUserId(2)).willReturn(jane);
    given(mongoTemplate.updateFirst(eq(query), any(Update.class), eq(ListingEnquiry.class)))
        .willReturn(UpdateResult.acknowledged(1L, 1L, null));

    // When
    ListingEnquiryMessageReply result =
        listingEnquiryService.addReplyMessage(enquiryMessageId, replyDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getSenderId()).isEqualTo(1);
    assertThat(result.getReceiverId()).isEqualTo(2);
    assertThat(result.getContent()).isEqualTo("Hello");
  }

  @Test
  @DisplayName("Enquiry ID not found")
  void givenInvalidEnquiryIdWhenAddReplyMessageThenThrowNotFoundException() {
    // Given
    String invalidEnquiryMessageId = "invalid-id";
    ListingEnquiryMessageReplyDTO replyDTO = new ListingEnquiryMessageReplyDTO(1, 2, "Hello");
    Query query = new Query(Criteria.where("_id").is(invalidEnquiryMessageId));

    given(userService.getUserByUserId(1)).willReturn(john);
    given(userService.getUserByUserId(2)).willReturn(jane);
    given(mongoTemplate.updateFirst(eq(query), any(Update.class), eq(ListingEnquiry.class)))
        .willReturn(UpdateResult.acknowledged(0L, 0L, null));

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> listingEnquiryService.addReplyMessage(invalidEnquiryMessageId, replyDTO));

    // Then
    assertThat(exception.getMessage()).isEqualTo("EnquiryId does not exist");
  }

  @Test
  @DisplayName("Reply DTO is null")
  void givenNullReplyDTOWhenAddReplyMessageThenThrowIllegalArgumentException() {
    // Given
    String enquiryMessageId = "64bd652852212f03ee0d2158";

    // When
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> listingEnquiryService.addReplyMessage(enquiryMessageId, null));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Message reply cannot be null or blank");
  }

  @Test
  @DisplayName("Sender or receiver user not found")
  void givenInvalidUserId_whenAddReplyMessage_thenThrowNotFoundException() {
    // Given
    String enquiryMessageId = "64bd652852212f03ee0d2158";
    ListingEnquiryMessageReplyDTO replyDTO = new ListingEnquiryMessageReplyDTO(99, 2, "Hello");

    given(userService.getUserByUserId(99)).willThrow(new NotFoundException("User not found"));

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> listingEnquiryService.addReplyMessage(enquiryMessageId, replyDTO));

    // Then
    assertThat(exception.getMessage()).isEqualTo("User not found");
  }

  @Test
  @DisplayName("Enquiry ID found but no modification made")
  void givenValidEnquiryIdButNoModification_whenAddReplyMessage_thenThrowNotFoundException() {
    // Given
    String enquiryMessageId = "64bd652852212f03ee0d2158";
    ListingEnquiryMessageReplyDTO replyDTO = new ListingEnquiryMessageReplyDTO(1, 2, "Hello");
    Query query = new Query(Criteria.where("_id").is(enquiryMessageId));

    given(userService.getUserByUserId(1)).willReturn(john);
    given(userService.getUserByUserId(2)).willReturn(jane);
    given(mongoTemplate.updateFirst(eq(query), any(Update.class), eq(ListingEnquiry.class)))
        .willReturn(UpdateResult.acknowledged(1L, 0L, null));

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> listingEnquiryService.addReplyMessage(enquiryMessageId, replyDTO));

    // Then
    assertThat(exception.getMessage()).isEqualTo("EnquiryId");
  }
}
