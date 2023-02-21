package com.distasilucas.cryptobalancetracker.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    Boolean isTokenValid(String token, UserDetails userDetails);
    String extractUsername(String token);

}
