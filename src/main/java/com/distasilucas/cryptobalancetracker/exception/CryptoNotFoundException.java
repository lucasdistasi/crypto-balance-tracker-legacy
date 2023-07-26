package com.distasilucas.cryptobalancetracker.exception;

import org.springframework.http.HttpStatus;

public class CryptoNotFoundException extends ApiException {

    public CryptoNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
