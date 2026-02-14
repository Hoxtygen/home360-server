/* (C)2025 */
package com.codeplanks.home360.utils;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import com.codeplanks.home360.domain.auth.SessionUserInfo;
import jakarta.servlet.http.HttpServletRequest;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionUserInfoUtil {

  private UserAgentParser parser;

  @PostConstruct
  public void init() {
    try {
      this.parser = new UserAgentService().loadParser();
    } catch (Exception e) {
      log.error("Failed to initialize UserAgentParser. User agent detection will be limited.", e);
    }
  }

  public SessionUserInfo extractSessionUserInfo(HttpServletRequest httpServletRequest) {
    String userAgentString = httpServletRequest.getHeader("User-Agent");
    String remoteAddress = httpServletRequest.getRemoteAddr();

    if (parser == null || userAgentString == null) {
      return SessionUserInfo.builder()
          .browserName("Unknown")
          .operatingSystem("Unknown")
          .deviceType("Unknown")
          .remoteAddress(remoteAddress)
          .build();
    }

    try {
      final Capabilities capabilities = parser.parse(userAgentString);
      return SessionUserInfo.builder()
          .browserName(capabilities.getBrowser())
          .operatingSystem(capabilities.getPlatform())
          .deviceType(capabilities.getDeviceType())
          .remoteAddress(remoteAddress)
          .build();
    } catch (Exception e) {
      log.warn("Failed to parse user agent string: {}", userAgentString);
      return SessionUserInfo.builder()
          .browserName("Unknown")
          .operatingSystem("Unknown")
          .deviceType("Unknown")
          .remoteAddress(remoteAddress)
          .build();
    }
  }
}
