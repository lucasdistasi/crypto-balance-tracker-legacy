package com.distasilucas.cryptobalancetracker.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String errorMessage;

    public ApiException(String message) {
        super(message);
        this.errorMessage = message;
    }
}
