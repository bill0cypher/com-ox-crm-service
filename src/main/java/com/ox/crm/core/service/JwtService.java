package com.ox.crm.core.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.ox.crm.core.exception.UnauthorizedException;
import com.ox.crm.core.properties.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final ApplicationProperties properties;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    if (userDetails.getAuthorities() != null) {
      claims.put("authorities", userDetails.getAuthorities());
    }
    return buildToken(claims, userDetails, getExpirationTime());
  }

  public long getExpirationTime() {
    return properties.getSecurity().getJwtExpirationInMs();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  public UserDetails getPrincipal() {
    return Optional.of(SecurityContextHolder.getContext().getAuthentication())
        .map(authentication -> ((UserDetails) authentication.getDetails()))
        .orElseThrow(UnauthorizedException::new);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    var parser = Jwts.parser()
        .setSigningKey(getSignInKey())
        .build();

    return parser.parseSignedClaims(token)
        .getPayload();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(properties.getSecurity().getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String buildToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails,
      long expiration
  ) {
    var currentDate = new Date(System.currentTimeMillis());
    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(currentDate)
        .expiration(DateUtils.addMilliseconds(currentDate, Math.toIntExact(expiration)))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }
}
