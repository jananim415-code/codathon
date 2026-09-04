package com.projectsphere.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${app.security.jwt-secret}") String secret,
                      @Value("${app.security.jwt-expiration-ms:3600000}") long expirationMs) {
        if (secret == null || secret.isBlank()) {
            this.signingKey = Jwts.SIG.HS256.key().build();
            this.expirationMs = expirationMs;
            return;
        }
        try {
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (RuntimeException ex) {
            throw new IllegalStateException("JWT secret must be valid base64 and at least 32 bytes", ex);
        }
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails user) {
        Date now = new Date();
        return Jwts.builder().subject(user.getUsername()).issuedAt(now)
            .expiration(new Date(now.getTime() + expirationMs)).signWith(signingKey).compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            return user.getUsername().equals(extractUsername(token)) && !parseClaims(token).getExpiration().before(new Date());
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public long getExpirationMs() { return expirationMs; }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }
}
