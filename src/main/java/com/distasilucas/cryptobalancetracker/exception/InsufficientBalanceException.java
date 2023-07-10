package com.distasilucas.cryptobalancetracker.exception;

public class InsufficientBalanceException extends ApiException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
