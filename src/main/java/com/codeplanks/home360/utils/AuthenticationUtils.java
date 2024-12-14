/* (C)2024 */
package com.codeplanks.home360.utils;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {
  @Autowired private Environment environment;

  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.isAuthenticated() && !isAnonymous();
  }

  public boolean isAnonymous() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication instanceof AnonymousAuthenticationToken;
  }

  public boolean isLocalEnvironment() {
    String[] activeProfiles = environment.getActiveProfiles();
    System.out.println("profiles: " + Arrays.toString(activeProfiles));
    for (String profile : activeProfiles) {
      if ("dev".equalsIgnoreCase(profile) || "docker".equalsIgnoreCase(profile)) {
        return true;
      }
    }
    return false;
  }
}
