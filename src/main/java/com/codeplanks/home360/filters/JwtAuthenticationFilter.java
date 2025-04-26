/* (C)2024-2025 */
package com.codeplanks.home360.filters;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.repository.BlacklistedTokenRepository;
import com.codeplanks.home360.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final JwtUtils jwtUtils;
  private final UserDetailsService userDetailsService;

  @Qualifier("handlerExceptionResolver")
  private final HandlerExceptionResolver resolver;

  private final BlacklistedTokenRepository blacklistedTokenRepository;
  Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

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

    final String authHeader = request.getHeader("Authorization");
    String username;
    String jwt;

    if (authHeader == null || !authHeader.startsWith("Bearer")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      jwt = authHeader.substring(7);

      if (isTokenBlacklisted(jwt)) {
        logger.warn(
            "Blacklisted JWT received for user (attempting to extract): {}",
            jwtService.extractUsername(jwt));

        sendUnauthorizedError(request, response, "Invalid JWT");
        return;
      }

      if (jwtUtils.validateToken(jwt)) {
        username = jwtService.extractUsername(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
          if (jwtService.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          }
        }
      }
    } catch (ExpiredJwtException | MalformedJwtException | IllegalArgumentException exception) {
      logger.error("JWT Error: {}", exception.getMessage());
      resolver.resolveException(request, response, null, exception);
      return;
    } catch (UnAuthorizedException exception) {
      logger.error("Blacklisted JWT error: {}", exception.getMessage());
      return;
    } catch (Exception exception) {
      logger.error("Unexpected error: {}", exception.getMessage());
      resolver.resolveException(request, response, null, exception);
      return;
    }
    filterChain.doFilter(request, response);
  }

  private boolean isTokenBlacklisted(String token) {
    return blacklistedTokenRepository.findByToken(token).isPresent();
  }

  private void sendUnauthorizedError(
      HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
    logger.warn("Sending unauthorized error from JWT filter: {}", message);
    resolver.resolveException(request, response, null, new UnAuthorizedException(message));
  }
}
