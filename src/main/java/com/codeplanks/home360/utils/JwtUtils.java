/* (C)2025 */
package com.codeplanks.home360.utils;

import com.codeplanks.home360.domain.auth.BlacklistedToken;
import com.codeplanks.home360.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
  private final BlacklistedTokenRepository blacklistedTokenRepository;
  private final String secretKey;

  @Autowired
  public JwtUtils(
      BlacklistedTokenRepository blacklistedTokenRepository,
      @Value("${application.security.jwt.secret-key}") String secretKey) {
    this.blacklistedTokenRepository = blacklistedTokenRepository;
    this.secretKey = secretKey;
  }

  public void invalidateToken(String token) {
    LocalDateTime expiryDate = extractExpiryDate(token);

    BlacklistedToken blacklistedToken = new BlacklistedToken();
    blacklistedToken.setToken(token);
    blacklistedToken.setExpiryDate(expiryDate);
    blacklistedTokenRepository.save(blacklistedToken);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigninKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public Key getSigninKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor((keyBytes));
  }

  public boolean validateToken(String token) {
    if (blacklistedTokenRepository.existsByToken(token)) {
      return false;
    }
    try {
      Jwts.parserBuilder().setSigningKey(getSigninKey()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException exception) {
      return false;
    }
  }

  public LocalDateTime extractExpiryDate(String token) {
    Claims claims = extractAllClaims(token);
    return claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public String extractRefreshTokenFromRequest(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
