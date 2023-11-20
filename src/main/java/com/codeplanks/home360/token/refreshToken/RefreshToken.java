package com.codeplanks.home360.token.refreshToken;


import com.codeplanks.home360.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String token;
  private Instant expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id")
  private AppUser user;

}
