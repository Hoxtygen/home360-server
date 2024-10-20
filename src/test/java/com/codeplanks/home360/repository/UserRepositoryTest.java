/* (C)2024 */
package com.codeplanks.home360.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {
  @Autowired private UserRepository userRepository;

  private AppUser user;

  @Value("${application.security.password}")
  private String userPassword;

  @BeforeEach
  void setUp() {
    user =
        AppUser.builder()
            .firstName("Magnifera")
            .lastName("Indica")
            .email("magnifera_indica@example.com")
            .role(Role.USER)
            .phoneNumber("09021234567")
            .address("221B, Baker street, London")
            .password(userPassword)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();
  }

  @Test
  void findByEmail() {
    // Given
    String userEmail = "magnifera_indica@example.com";
    userRepository.save(user);

    // When
    Optional<AppUser> result = userRepository.findByEmail(userEmail);

    // Then
    assertAll(
        () -> assertThat(result).isPresent(),
        () -> assertThat(result.get().getEmail()).isEqualTo("magnifera_indica@example.com"),
        () -> assertThat(result.get().getFirstName()).isEqualTo("Magnifera"),
        () -> assertThat(result.get().getLastName()).isEqualTo("Indica"),
        () -> assertThat(result.get().getEmail()).isEqualTo(userEmail),
        () -> assertThat(result.get().getAddress()).isEqualTo("221B, Baker street, London"),
        () -> assertThat(result.get().getPhoneNumber()).isEqualTo("09021234567"));
  }

  @Test
  void findByPhoneNumber() {
    // Given
    String userPhoneNumber = "09021234567";
    userRepository.save(user);

    // When
    Optional<AppUser> result = userRepository.findByPhoneNumber(userPhoneNumber);
    // Then
    assertAll(
        () -> assertThat(result).isPresent(),
        () -> assertThat(result.get().getPhoneNumber()).isEqualTo("09021234567"),
        () -> assertThat(result.get().getEmail()).isEqualTo("magnifera_indica@example.com"),
        () -> assertThat(result.get().getFirstName()).isEqualTo("Magnifera"),
        () -> assertThat(result.get().getLastName()).isEqualTo("Indica"),
        () -> assertThat(result.get().getAddress()).isEqualTo("221B, Baker street, London"));
  }
}
