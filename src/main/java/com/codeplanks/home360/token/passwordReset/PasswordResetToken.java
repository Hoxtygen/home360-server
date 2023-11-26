package com.codeplanks.home360.token.passwordReset;


import com.codeplanks.home360.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

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
  private Integer token_id;
  private String token;
  private Date expirationTime;
  private static final int EXPIRATION_TIME = 10;

  @OneToOne
  @JoinColumn(name = "user_id")
  private AppUser user;

  public PasswordResetToken(String token, AppUser user) {
    super();
    this.token = token;
    this.user = user;
    this.expirationTime = this.getTokenExpirationTime();
  }

  public PasswordResetToken(String token) {
    super();
    this.token = token;
    this.expirationTime = this.getTokenExpirationTime();
  }

  public Date getTokenExpirationTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
    return new Date(calendar.getTime().getTime());
  }
}
