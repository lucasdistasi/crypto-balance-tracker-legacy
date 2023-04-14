package com.distasilucas.cryptobalancetracker.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiException extends RuntimeException {

    private final String errorMessage;
    private final HttpStatusCode httpStatusCode;

    public ApiException(String message) {
        super(message);
        this.errorMessage = message;
        this.httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(String message, Throwable ex) {
        super(message, ex);
        this.errorMessage = message;
        this.httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.errorMessage = message;
        this.httpStatusCode = httpStatusCode;
    }
}
