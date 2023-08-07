package com.codeplanks.home360.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
  @Autowired
  private UserRepository userRepositoryTest;
  @AfterEach
  void tearDown() {
    userRepositoryTest.deleteAll();
  }

  @Test
  void testFindByEmail() {
    // Given
String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    AppUser user =
            AppUser.builder().firstName("Shaolin").lastName("Dragon").email("shaolin@example.com")
                    .address("Ketu Alapere").password("Int3rnat!onalization")
                    .phoneNumber("09087812347")
                    .createdAt(new Date()).updatedAt(new Date())
                    .role(Role.USER)
                    .build();
    userRepositoryTest.save(user);

    // When
    Optional<AppUser> userEmail = userRepositoryTest.findByEmail("shaolin@example.com");

    // Then
    Assertions.assertThat(userEmail).isPresent();
    Assertions.assertThat(userEmail.get().getEmail()).isEqualTo("shaolin@example.com");
    Assertions.assertThat(userEmail.get().getEmail()).isNotEqualTo("dragon@yahoo.com");
    Assertions.assertThat(userEmail.get().getEmail()).isNotEmpty();
    Assertions.assertThat(userEmail.get().getEmail()).matches(regexPattern);



  }

  @Test
  void testFindByPhoneNumber() {
    // Given
    AppUser user =
            AppUser.builder().firstName("Shaolin").lastName("Dragon").email("shaolin@example.com")
                    .address("Ketu Alapere").password("Int3rnat!onalization")
                    .phoneNumber("09087812347")
                    .createdAt(new Date()).updatedAt(new Date())
                    .role(Role.USER)
                    .build();
    userRepositoryTest.save(user);

    // When
    Optional<AppUser> userWithPhoneNumber = userRepositoryTest.findByPhoneNumber("09087812347");

    // Then
    Assertions.assertThat(userWithPhoneNumber).isPresent();
    Assertions.assertThat(userWithPhoneNumber.get().getPhoneNumber()).isNotEqualTo("09087812343");
    Assertions.assertThat(userWithPhoneNumber.get().getPhoneNumber()).isNotEmpty();
//    Assertions.assertThat(userWithPhoneNumber.get().getPhoneNumber()).matches();


  }
}