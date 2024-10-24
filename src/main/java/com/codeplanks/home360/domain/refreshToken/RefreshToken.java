package com.codeplanks.home360.domain.refreshToken;


import com.codeplanks.home360.domain.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Wasiu Idowu
 * */

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
  private LocalDateTime expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id")
  private AppUser user;

}
