package com.distasilucas.cryptobalancetracker.exception;

import lombok.Getter;
import org.everit.json.schema.ValidationException;

import java.util.Collections;
import java.util.List;

@Getter
public class ApiValidationException extends ApiException {

    private final List<ValidationException> causingExceptions;
    private final String errorMessage;

    public ApiValidationException(String message) {
        super(message);
        this.errorMessage = message;
        causingExceptions = Collections.emptyList();
    }

    public ApiValidationException(List<ValidationException> causingExceptions, String errorMessage, String message) {
        super(message);
        this.errorMessage = errorMessage;
        this.causingExceptions = causingExceptions;
    }
}
