package com.distasilucas.cryptobalancetracker.exception;

import org.springframework.http.HttpStatus;

public class CoinNotFoundException extends ApiException {

    public CoinNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
