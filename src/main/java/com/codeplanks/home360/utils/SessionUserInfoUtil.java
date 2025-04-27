/* (C)2025 */
package com.codeplanks.home360.utils;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import com.codeplanks.home360.domain.auth.SessionUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionUserInfoUtil {
  public static SessionUserInfo extractSessionUserInfo(HttpServletRequest httpServletRequest)
      throws IOException, ParseException {

    String userAgentString = httpServletRequest.getHeader("User-Agent");

    final UserAgentParser parser = new UserAgentService().loadParser();

    final Capabilities capabilities = parser.parse(userAgentString);

    final String browser = capabilities.getBrowser();
    final String deviceType = capabilities.getDeviceType();
    final String platform = capabilities.getPlatform();

    String remoteAddress = httpServletRequest.getRemoteAddr();

    return SessionUserInfo.builder()
        .browserName(browser)
        .operatingSystem(platform)
        .deviceType(deviceType)
        .remoteAddress(remoteAddress)
        .build();
  }
}
