package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.User;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.distasilucas.cryptobalancetracker.constant.Constants.TOKEN_EXPIRED;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceImplTest {

    private final JwtService jwtService = new JwtServiceImpl("741C4AB93C0B3257FA14BF418BE24F9678A022862D7A7AA77C8C7397B1E4DA66");
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNhcyIsImlhdCI6MTY3NzYyNjI1NiwiZXhwIjoxOTkzMjQ1NDU2fQ.rXsZUCh8cRXoZW712O1BCGjUsONUoO0nRiBGlYwPeu8";
    private static final String MALFORMED_TOKEN = "eyJhbGciOiJIUzI1NiJ9aeyJzdWIiOiJsdWNhcyIsImlhdCI6MTY3NzYyNjI1NiwiZXhwIjoxOTkzMjQ1NDU2fQ.rXsZUCh8cRXoZW712O1BCGjUsONUoO0nRiBGlYwPeu8";
    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNhcyIsImlhdCI6MTY3NzYyNjk0MywiZXhwIjoxNjc3NjI2OTMzfQ.eG5dlO40efrT9PXN6tWetyX6io3OxLTLO6ZR447zDXg";

    @Test
    void shouldValidateNonExpiredToken() {
        var user = User.builder()
                .username("lucas")
                .build();

        var valid = jwtService.isTokenValid(TOKEN, user);

        assertTrue(valid);
    }

    @Test
    void shouldThrowApiExceptionWithExpiredToken() {
        var user = User.builder()
                .username("lucas")
                .build();

        var apiException = assertThrows(ApiException.class,
                () -> jwtService.isTokenValid(EXPIRED_TOKEN, user));

        assertAll(
                () -> assertEquals(TOKEN_EXPIRED, apiException.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, apiException.getHttpStatusCode())
        );
    }

    @Test
    void shouldThrowApiExceptionWhenNoCaughtExceptionIsThrown() {
        var user = User.builder()
                .username("lucas")
                .build();

        var apiException = assertThrows(ApiException.class,
                () -> jwtService.isTokenValid(MALFORMED_TOKEN, user));

        assertAll(
                () -> assertEquals(UNKNOWN_ERROR, apiException.getMessage()),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiException.getHttpStatusCode())
        );
    }

    @Test
    void shouldReturnFalseWithInvalidUsername() {
        var user = User.builder()
                .username("John")
                .build();

        var valid = jwtService.isTokenValid(TOKEN, user);

        assertFalse(valid);
    }

    @Test
    void shouldExtractUsername() {
        String username = jwtService.extractUsername(TOKEN);

        assertEquals("lucas", username);
    }
}