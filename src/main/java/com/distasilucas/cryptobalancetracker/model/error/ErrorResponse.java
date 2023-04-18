package com.distasilucas.cryptobalancetracker.model.error;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int statusCode,
        List<Error> errors,
        LocalDateTime timeStamp
) {

    public ErrorResponse(int statusCode, List<Error> errors) {
        this(statusCode, errors, LocalDateTime.now());
    }
}
