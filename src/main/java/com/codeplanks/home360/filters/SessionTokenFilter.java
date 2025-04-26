/* (C)2025 */
package com.codeplanks.home360.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class SessionTokenFilter extends OncePerRequestFilter {
  private final RedisTemplate<String, Object> redisTemplate;

  private final HandlerExceptionResolver resolver;

  public SessionTokenFilter(
      RedisTemplate<String, Object> redisTemplate,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.redisTemplate = redisTemplate;
    this.resolver = resolver;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getServletPath().contains("/api/v1/auth")
        || request.getServletPath().equals("/api/v1")) {
      filterChain.doFilter(request, response);
      return;
    }
    Optional<String> sessionTokenOptional = extractSessionToken(request);

    if (sessionTokenOptional.isPresent()) {
      String sessionToken = sessionTokenOptional.get();

      String userEmail =
          (String) redisTemplate.opsForHash().get("session:" + sessionToken, "email");

      if (userEmail != null) {
        String activeSessionToken =
            (String) redisTemplate.opsForValue().get("active_session:" + userEmail);
        if (sessionToken.equals(activeSessionToken)) {
          request.setAttribute("sessionValid", true);
          request.setAttribute("sessionUserEmail", userEmail);

          logger.info("Valid session token found for user: " + userEmail);
          filterChain.doFilter(request, response);
          return;
        }

      } else {
        logger.warn("Invalid or expired session token: {}" + sessionToken);
        sendUnauthorizedError(request, response);
      }
      return;
    }
    logger.debug("No session token found in request.");
    filterChain.doFilter(request, response);
  }

  private Optional<String> extractSessionToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
      return Arrays.stream(request.getCookies())
          .filter(cookie -> "sessionToken".equals(cookie.getName()))
          .findFirst()
          .map(Cookie::getValue)
          .filter(StringUtils::hasText);
    }
    return Optional.empty();
  }

  private void sendUnauthorizedError(HttpServletRequest request, HttpServletResponse response) {
    logger.warn(
        "Sending unauthorized error for session token validation: {}"
            + "Invalid or expired session token");
    resolver.resolveException(
        request, response, null, new AccessDeniedException("Invalid or expired session token"));
  }
}
