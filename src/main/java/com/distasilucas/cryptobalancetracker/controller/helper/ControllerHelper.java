package com.distasilucas.cryptobalancetracker.controller.helper;

import org.springframework.http.HttpStatus;

import java.util.Optional;

public interface ControllerHelper {

    default <T> HttpStatus getHttpStatusCode(Optional<T> response) {
        return response.isPresent() ? HttpStatus.OK : HttpStatus.NO_CONTENT;
    }
}
