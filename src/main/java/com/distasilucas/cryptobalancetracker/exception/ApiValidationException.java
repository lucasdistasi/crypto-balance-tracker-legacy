package com.distasilucas.cryptobalancetracker.exception;

import lombok.Getter;
import org.everit.json.schema.ValidationException;

import java.util.Collections;
import java.util.List;

@Getter
public class ApiValidationException extends ApiException {

    private final List<ValidationException> causingExceptions;

    public ApiValidationException(String message) {
        super(message);
        causingExceptions = Collections.emptyList();
    }

    public ApiValidationException(List<ValidationException> causingExceptions, String message) {
        super(message);
        this.causingExceptions = causingExceptions;
    }
}
