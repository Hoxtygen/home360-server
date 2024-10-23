/* (C)2024 */
package com.codeplanks.home360.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {
  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.isAuthenticated() && !isAnonymous();
  }

  public boolean isAnonymous() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication instanceof AnonymousAuthenticationToken;
  }
}
