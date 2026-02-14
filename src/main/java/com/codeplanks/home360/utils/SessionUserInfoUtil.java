/* (C)2025 */
package com.codeplanks.home360.utils;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.codeplanks.home360.domain.auth.SessionUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionUserInfoUtil {

  private final UserAgentParser parser;

  public SessionUserInfo extractSessionUserInfo(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    String remoteAddress =
            Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                    .orElse(request.getRemoteAddr());

    if (userAgent == null) {
      return unknown(remoteAddress);
    }

    try {
      Capabilities capabilities = parser.parse(userAgent);
      return SessionUserInfo.builder()
              .browserName(capabilities.getBrowser())
              .operatingSystem(capabilities.getPlatform())
              .deviceType(capabilities.getDeviceType())
              .remoteAddress(remoteAddress)
              .build();
    } catch (Exception e) {
      log.debug("Failed to parse user agent", e);
      return unknown(remoteAddress);
    }
  }

  private SessionUserInfo unknown(String remoteAddress) {
    return SessionUserInfo.builder()
            .browserName("Unknown")
            .operatingSystem("Unknown")
            .deviceType("Unknown")
            .remoteAddress(remoteAddress)
            .build();
  }
}
