package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.signing-key}")
    private String JWT_SIGNING_KEY;

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token {} for {}", token, userDetails.getUsername());

        return extractUsername(token).equals(userDetails.getUsername()) &&
                isTokenNonExpired(token);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenNonExpired(String token) {
        Date date = extractClaim(token, Claims::getExpiration);

        return date.after(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claims) {
        Claims claim = extractClaims(token);

        return claims.apply(claim);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] decoders = Decoders.BASE64.decode(JWT_SIGNING_KEY);

        return Keys.hmacShaKeyFor(decoders);
    }
}
