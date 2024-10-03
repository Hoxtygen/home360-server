package com.codeplanks.home360.user;


import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTests {
  @Autowired
  private UserRepository userRepository;

  private AppUser user;

  @Value("${application.security.password}")
  private String userPassword;

  @BeforeEach
  public void setup() {
    user = AppUser.builder()
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

  // JUnit test for save user operation
  @DisplayName("save user")
  @Test
  public void givenUserObject_whenSave_thenReturnSavedUser() {
    // When - action or the behaviour we're testing for
    AppUser savedUser = userRepository.save(user);

    // Then - verify the output
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo("magnifera_indica@example.com");
    assertThat(savedUser.getFirstName()).isEqualTo("Magnifera");
    assertThat(savedUser.getLastName()).isEqualTo("Indica");
    assertThat(savedUser.getPhoneNumber()).isEqualTo("09021234567");
    assertThat(savedUser.getRole().toString()).isEqualTo("USER");

    assertThat(savedUser.getEmail()).isNotEqualTo("magnifera_indicated@example.com");
    assertThat(savedUser.getFirstName()).isNotEqualTo("Magnifier");
    assertThat(savedUser.getLastName()).isNotEqualTo("Indicated");
    assertThat(savedUser.getPhoneNumber()).isNotEqualTo("09021234576");
    assertThat(savedUser.getRole().toString()).isNotEqualTo("User");
  }

  // JUnit test for find user by phone number
  @DisplayName("get user by phone number")
  @Test
  public void givenUserPhoneNumber_whenFindByPhoneNumber_thenReturnUserObject() {
    // Given - precondition or setup
    userRepository.save(user);

    // When - action or the behaviour we're testing for
    AppUser userDB = userRepository.findByPhoneNumber(user.getPhoneNumber()).get();

    // Then - verify the output
    assertThat(userDB).isNotNull();
    assertThat(userDB.getPhoneNumber()).isEqualTo("09021234567");
    assertThat(userDB.getPhoneNumber()).isNotEqualTo("09022134567");
  }

  // JUnit test for find user by email
  @DisplayName("get user by email")
  @Test
  public void givenUserEmail_whenFindByEmail_thenReturnUserObject() {
    // Given - precondition or setup
    String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    userRepository.save(user);

    // When - action or the behaviour we're testing for
    AppUser userDB = userRepository.findByEmail(user.getEmail()).get();

    // Then - verify the output
    assertThat(userDB).isNotNull();
    assertThat(userDB.getEmail()).isEqualTo("magnifera_indica@example.com");
    assertThat(userDB.getEmail()).isNotEqualTo("magniferas_indica@example.com");
    assertThat(userDB.getEmail()).matches(regexPattern);
  }

  // JUnit test for find user by id
  @DisplayName("get user by id")
  @Test
  public void givenUserObject_whenFindById_thenReturnUserObject() {
    // Given - precondition or setup
    userRepository.save(user);

    // When - action or the behaviour we're testing for
    AppUser appUser = userRepository.findById(user.getId()).get();

    // Then - verify the output
    assertThat(appUser).isNotNull();
    assertThat(appUser.getId()).isGreaterThan(0);
  }

  // JUnit test for getting all users
  @DisplayName("get all users")
  @Test
  public void givenUsersList_whenFindAll_thenReturnUsersList() {
    // Given - precondition or setup
    AppUser user2 = AppUser.builder()
            .firstName("Carica")
            .lastName("Papaya")
            .email("carica@example.com")
            .address("Ketu Alapere").password(userPassword)
            .phoneNumber("09087812347")
            .createdAt(new Date())
            .updatedAt(new Date())
            .role(Role.USER)
            .build();
    userRepository.save(user);
    userRepository.save(user2);

    // When - action or the behaviour we're testing for
    List<AppUser> userList = userRepository.findAll();

    // Then - verify the output
    assertThat(userList).isNotNull();
    assertThat(userList.size()).isEqualTo(2);

    assertThat(userList).isNotEmpty();
  }
}
