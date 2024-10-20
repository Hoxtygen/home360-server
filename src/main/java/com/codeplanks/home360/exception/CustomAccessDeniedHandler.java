package com.codeplanks.home360.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  public static final Logger LOG =
      Logger.getLogger(String.valueOf(CustomAccessDeniedHandler.class));

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
      throws IOException, ServletException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      LOG.warning(
          "User: "
              + auth.getName()
              + " attempted to access the protected URL: "
              + request.getRequestURI());
    }

    response.sendRedirect(request.getContextPath() + "/accessDenied");
  }
}
