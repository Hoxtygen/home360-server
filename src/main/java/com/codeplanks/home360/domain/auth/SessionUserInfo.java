/* (C)2025 */
package com.codeplanks.home360.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SessionUserInfo {
  private String browserName;
  private String operatingSystem;
  private String deviceType;
  private String remoteAddress;
}
