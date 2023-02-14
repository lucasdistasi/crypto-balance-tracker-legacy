package com.distasilucas.cryptobalancetracker.exception;

public class CoinNotFoundException extends ApiValidationException {
    public CoinNotFoundException(String message) {
        super(message);
    }
}
