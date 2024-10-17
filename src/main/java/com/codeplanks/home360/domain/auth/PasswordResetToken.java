/* (C)2024 */
package com.codeplanks.home360.domain.auth;

import com.codeplanks.home360.domain.user.AppUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wasiu Idowu
 * */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String token;
  private LocalDateTime expirationTime;
  private LocalDateTime created_time;
  private static final int EXPIRATION_TIME = 10;

  @OneToOne
  @JoinColumn(name = "user_id")
  private AppUser user;

  public PasswordResetToken(String token, AppUser user) {
    this.token = token;
    this.user = user;
    this.created_time = LocalDateTime.now();
    this.expirationTime = getTokenExpirationTime();
  }

  public LocalDateTime getTokenExpirationTime() {
    return created_time.plusMinutes(EXPIRATION_TIME);
  }
}
