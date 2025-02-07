/* (C)2025 */
package com.codeplanks.home360.domain.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity(name = "blacklisted_tokens")
@Table(name = "blacklisted_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {

  @Id
  @SequenceGenerator(
      name = "blacklisted_tokens_id_sequence",
      sequenceName = "blacklisted_tokens_id_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blacklisted_tokens_id_sequence")
  private Long id;

  private String token;
  private LocalDateTime expiryDate;

  @Column(
      name = "createdAt",
      nullable = false,
      columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
  @CreationTimestamp
  private LocalDateTime createdAt;
}
