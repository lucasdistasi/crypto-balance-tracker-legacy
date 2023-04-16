package com.distasilucas.cryptobalancetracker.exception;

import org.springframework.http.HttpStatus;

public class PlatformNotFoundException extends ApiException {

    public PlatformNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
