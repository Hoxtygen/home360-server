/* (C)2024 */
package com.codeplanks.home360.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RateLimitingFilter implements Filter {
  private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Map<String, Bucket> authenticatedBucketCache = new ConcurrentHashMap<>();
  private final Map<String, Bucket> unAuthenticatedBucketCache = new ConcurrentHashMap<>();
  private final Map<String, Bucket> loginBucketCache = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String clientIpAddress = request.getRemoteAddr();
    boolean isAuthenticated = httpServletRequest.getUserPrincipal() != null;

    String path = httpServletRequest.getRequestURI();
    System.out.println("Path: " + path);

    if (path.startsWith("/swagger") || path.startsWith("/v1/api-docs")) {
      chain.doFilter(request, response);
      return;
    }

    if (path.equals("/api/v1/auth/login")) {
      Bucket loginBucket =
          loginBucketCache.computeIfAbsent(clientIpAddress, bucket -> createLoginBucket());
      logger.debug(
          "Client IP: {}, Bucket Tokens Available: {}",
          clientIpAddress,
          loginBucket.getAvailableTokens());

      if (!loginBucket.tryConsume(1)) {
        sendErrorResponse(httpResponse, "Too many login attempts. Please try again later.");
        return;
      }
    } else if (isAuthenticated) {
      String userId = httpServletRequest.getUserPrincipal().getName();
      Bucket authBucket =
          authenticatedBucketCache.computeIfAbsent(
              userId, newBucket -> createNewAuthenticatedBucket());
      if (!authBucket.tryConsume(1)) {
        logger.debug("rate limiting abuse: " + userId);
        sendErrorResponse(httpResponse, "Rate limit exceeded for authenticated user.");
        return;
      }
    } else {
      Bucket unAuthBucket =
          unAuthenticatedBucketCache.computeIfAbsent(
              clientIpAddress, newBucket -> createNewUnAuthenticatedBucket());
      if (!unAuthBucket.tryConsume(1)) {
        logger.debug("rate limiting abuse: " + clientIpAddress);
        sendErrorResponse(httpResponse, "Rate limit exceeded for unauthenticated user.");
        return;
      }
    }
    chain.doFilter(request, response);
  }

  private Bucket createNewAuthenticatedBucket() {
    return Bucket.builder()
        .addLimit(
            limit -> limit.capacity(50).refillGreedy(50, Duration.ofMinutes(1)).initialTokens(50))
        .build();
  }

  private Bucket createNewUnAuthenticatedBucket() {
    return Bucket.builder()
        .addLimit(
            limit -> limit.capacity(30).refillGreedy(30, Duration.ofMinutes(5)).initialTokens(30))
        .build();
  }

  private Bucket createLoginBucket() {
    return Bucket.builder()
        .addLimit(
            limit -> limit.capacity(5).refillIntervally(5, Duration.ofMinutes(1)).initialTokens(5))
        .build();
  }

  private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType("application/json");

    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", getCurrentTimestamp());
    errorDetails.put("status", HttpStatus.TOO_MANY_REQUESTS);
    errorDetails.put("message", message);

    String jsonResponse = objectMapper.writeValueAsString(errorDetails);
    response.getWriter().write(jsonResponse);
    logger.debug("Rate limiting Error: " + message);
  }

  private String getCurrentTimestamp() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    return LocalDateTime.now().format(formatter);
  }
}
