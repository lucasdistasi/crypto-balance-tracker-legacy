package com.distasilucas.cryptobalancetracker.exception;

public class CoinNotFoundException extends ApiException {
    public CoinNotFoundException(String message) {
        super(message);
    }
}
