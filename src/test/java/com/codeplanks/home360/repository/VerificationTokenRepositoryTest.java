/* (C)2024 */
package com.codeplanks.home360.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class VerificationTokenRepositoryTest {
  @Autowired private VerificationTokenRepository verificationTokenRepository;

  private VerificationToken verificationToken;
  private AppUser appUser;

  @Value("${application.security.password}")
  private String userPassword;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    verificationToken = new VerificationToken();
    appUser =
        AppUser.builder()
            .firstName("Elaeis")
            .lastName("Guineensis")
            .email("elaeis@example.com")
            .address("221B, Baker street, London")
            .phoneNumber("08030123456")
            .password(userPassword)
            .role(Role.USER)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();
  }

  @Test
  @DisplayName("find saved token")
  void GivenValidVerificationTokenWhenQueriedThenReturnToken() {
    // Given
    String uuidToken = UUID.randomUUID().toString();
    VerificationToken token = VerificationToken.builder().token(uuidToken).user(appUser).build();
    userRepository.save(appUser);
    verificationTokenRepository.save(token);

    // When
    VerificationToken result = verificationTokenRepository.findByToken(uuidToken);

    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.getToken()).isEqualTo(uuidToken));
  }

  @Test
  @DisplayName("Find non-existent token")
  public void GivenValidTokenWhenQueriedForUnsavedTokenThenReturnNull() {
    // Given
    String nonExistentToken = UUID.randomUUID().toString();

    // When
    VerificationToken result = verificationTokenRepository.findByToken(nonExistentToken);

    // Then
    assertThat(result).isNull(); // Expecting null since the token doesn't exist
  }

  @Test
  @DisplayName("Delete token successfully")
  void GivenValidTokenWhenDeletedAndQueriedForThenReturnNull() {
    // Given
    String uuidToken = UUID.randomUUID().toString();
    VerificationToken token = VerificationToken.builder().token(uuidToken).user(appUser).build();
    String uuidToken1 = UUID.randomUUID().toString();
    VerificationToken token1 = VerificationToken.builder().token(uuidToken1).user(appUser).build();
    userRepository.save(appUser);
    verificationTokenRepository.save(token);
    verificationTokenRepository.save(token1);

    // When
    verificationTokenRepository.deleteByToken(uuidToken);
    VerificationToken result = verificationTokenRepository.findByToken(uuidToken);
    VerificationToken result1 = verificationTokenRepository.findByToken(uuidToken1);

    // Then
    assertThat(result).isNull();
    assertAll(
        () -> assertThat(result1).isNotNull(),
        () -> assertThat(result1.getToken()).isEqualTo(uuidToken1));
  }
}
