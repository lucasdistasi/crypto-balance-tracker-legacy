package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import static com.distasilucas.cryptobalancetracker.constant.Constants.TOKEN_EXPIRED;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN_ERROR;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final String jwtSigningKey;

    public JwtServiceImpl(@Value("${jwt.signing-key}") String jwtSigningKey) {
        this.jwtSigningKey = jwtSigningKey;
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating JWT token for {}", userDetails.getUsername());

        return isTokenNonExpired(token) && extractUsername(token).equals(userDetails.getUsername());
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new ApiException(TOKEN_EXPIRED, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.warn("Exception when parsing JWT Token: {}", ex.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    private Key getSigningKey() {
        byte[] decoders = Decoders.BASE64.decode(jwtSigningKey);

        return Keys.hmacShaKeyFor(decoders);
    }
}
