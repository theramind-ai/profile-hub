package com.profilehub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret:TXlWZXJ5U2VjdXJlSldUU2VjcmV0S2V5MzJCeXRlc0xvbmdGb3JQcm9kdWN0aW9uVXNl}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:86400000}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        try {
            log.debug("JWT Secret length: {}", jwtSecret != null ? jwtSecret.length() : 0);
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT Secret - must be valid Base64: {}", e.getMessage());
            throw new RuntimeException("JWT_SECRET environment variable is not set or invalid. Set a valid Base64 string.", e);
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
            .subject(userPrincipal.getId().toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public String generateTokenFromId(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            log.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    public long getExpirationTime() {
        return jwtExpirationMs;
    }
}
