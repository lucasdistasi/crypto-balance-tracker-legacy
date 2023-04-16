package com.distasilucas.cryptobalancetracker.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedPlatformCoinException extends ApiException {

    public DuplicatedPlatformCoinException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
