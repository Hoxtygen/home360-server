/* (C)2024 */
package com.codeplanks.home360.domain.verificationToken;

import com.codeplanks.home360.domain.user.AppUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/**
 * @author Wasiu Idowu
 */
@Entity(name = "verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {
  private static final int EXPIRATION_TIME = 48;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String token;
  private LocalDateTime expirationTime;

  @OneToOne
  @JoinColumn(name = "user_id")
  private AppUser user;

  public VerificationToken(String token, AppUser user) {
    super();
    this.token = token;
    this.user = user;
    this.expirationTime = this.getTokenExpirationTime();
  }

  public VerificationToken(String token) {
    super();
    this.token = token;
    this.expirationTime = this.getTokenExpirationTime();
  }

  public VerificationToken(String token, AppUser user, Integer id) {
    super();
    this.token = token;
    this.user = user;
    this.id = id;
    this.expirationTime = this.getTokenExpirationTime();
  }

  public LocalDateTime getTokenExpirationTime() {
    return LocalDateTime.now().plusHours(EXPIRATION_TIME);
  }
}
