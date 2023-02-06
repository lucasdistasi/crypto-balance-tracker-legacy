package com.distasilucas.cryptobalancetracker.exception;

import lombok.Getter;
import org.everit.json.schema.ValidationException;

import java.util.List;

@Getter
public class ApiValidationException extends ApiException {

    private List<ValidationException> causingExceptions;
    private String errorMessage;

    public ApiValidationException(String message) {
        super(message);
    }

    public ApiValidationException(List<ValidationException> causingExceptions, String errorMessage, String message) {
        super(message);
        this.errorMessage = errorMessage;
        this.causingExceptions = causingExceptions;
    }
}
