package com.codeplanks.home360.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
  private JwtService jwtService;
  private static final String AUTH_HEADER_PARAMETER_AUTHORIZATION = "Authorization";
  private UserDetailsService userDetailsService;


  @Override
  public boolean preHandle(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull Object handler
  ) throws Exception {
    String jwtToken = null;
    final String userEmail;

    try {
      jwtToken =
              request.getHeader(AUTH_HEADER_PARAMETER_AUTHORIZATION).split(" ")[1].trim();
      userEmail = jwtService.extractUsername(jwtToken);
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      return jwtService.isTokenValid(jwtToken, userDetails);
    } catch (Exception exception) {
      System.out.println("Authentication error: " + exception.getMessage());
    }
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    return false;
  }


}

