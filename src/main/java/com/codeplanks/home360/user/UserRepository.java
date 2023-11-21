package com.codeplanks.home360.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
  Optional<AppUser> findByEmail(String email);
  Optional<AppUser> findByPhoneNumber(String phoneNumber);
}
