/* (C)2024 */
package com.codeplanks.home360.domain.verificationToken;

import com.codeplanks.home360.domain.user.AppUser;
import jakarta.persistence.*;
import java.util.Calendar;
import java.util.Date;
import lombok.*;

/**
 * @author Wasiu Idowu
 */
@Entity(name = "verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
  private static final int EXPIRATION_TIME = 48;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String token;
  private Date expirationTime;

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

  public Date getTokenExpirationTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.HOUR, EXPIRATION_TIME);
    return new Date(calendar.getTime().getTime());
  }
}
