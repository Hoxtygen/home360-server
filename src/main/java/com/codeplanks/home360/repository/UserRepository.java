package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
  Optional<AppUser> findByEmail(String email);
  Optional<AppUser> findByPhoneNumber(String phoneNumber);
}
