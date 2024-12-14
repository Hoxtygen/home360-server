/* (C)2024 */
package com.codeplanks.home360.domain.auth;

import com.codeplanks.home360.domain.token.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wasiu Idowu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
  private String firstName;
  private String lastName;
  private TokenResponse token;
}
