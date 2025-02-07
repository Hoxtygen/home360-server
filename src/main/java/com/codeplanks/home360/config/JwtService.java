/* (C)2025 */
package com.codeplanks.home360.config;

import com.codeplanks.home360.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  @Value("${application.security.jwt.jwtTokenExpirationMs}")
  private int jwtExpiration;

  @Autowired private JwtUtils jwtUtils;

  public String extractUsername(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = jwtUtils.extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * generate token from just user details
   *
   * @param userDetails  The details of the user
   * */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
        .signWith(jwtUtils.getSigninKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals((userDetails.getUsername())) && !isTokenExpired(token));
  }

  public Boolean isTokenExpired(String token) {
    try {
      return extractExpiration(token).before(new Date());
    } catch (RuntimeException exception) {
      throw new RuntimeException(exception.getMessage(), exception);
    }
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
