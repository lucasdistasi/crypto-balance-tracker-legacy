package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.DuplicatedPlatformCoinException;
import com.distasilucas.cryptobalancetracker.model.Error;
import com.distasilucas.cryptobalancetracker.model.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.everit.json.schema.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = CoinNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCoinNotFoundException(CoinNotFoundException coinNotFoundException) {
        log.warn("An CoinNotFoundException has occurred: ", coinNotFoundException);

        Error error = new Error(coinNotFoundException.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(Collections.singletonList(error));
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimeStamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(value = ApiValidationException.class)
    public ResponseEntity<ErrorResponse> handleApiValidationException(ApiValidationException apiValidationException) {
        log.warn("An ApiValidationException has occurred: ", apiValidationException);

        String apiValidationExceptionMessage = apiValidationException.getMessage();
        String message = apiValidationExceptionMessage != null ?
                apiValidationExceptionMessage :
                apiValidationException.getErrorMessage();
        List<ValidationException> causingExceptions = apiValidationException.getCausingExceptions();
        List<Error> errors = CollectionUtils.isNotEmpty(causingExceptions) ?
                causingExceptions.stream()
                        .map(validationException -> new Error(validationException.getErrorMessage()))
                        .toList() :
                Collections.singletonList(new Error(message));

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimeStamp(LocalDateTime.now());
        errorResponse.setErrors(errors);

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
        log.warn("An HttpMessageNotReadableException has occurred: ", httpMessageNotReadableException);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimeStamp(LocalDateTime.now());

        String originalMessage = UNKNOWN_ERROR;

        Throwable rootCause = httpMessageNotReadableException.getRootCause();
        if (rootCause instanceof InvalidFormatException invalidFormatException) {
            originalMessage = invalidFormatException.getOriginalMessage();
        }

        if (rootCause instanceof JsonParseException jsonParseException) {
            originalMessage = jsonParseException.getOriginalMessage();
        }

        Error error = new Error(originalMessage);
        errorResponse.setErrors(Collections.singletonList(error));

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(value = DuplicatedPlatformCoinException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedPlatformCoinException(DuplicatedPlatformCoinException duplicatedPlatformCoinException) {
        log.warn("An DuplicatedPlatformCoinException has occurred: ", duplicatedPlatformCoinException);

        Error error = new Error(duplicatedPlatformCoinException.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(Collections.singletonList(error));
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimeStamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException apiException) {
        log.warn("An ApiException has occurred: ", apiException);

        HttpStatusCode httpStatusCode = apiException.getHttpStatusCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(new Error(apiException.getErrorMessage())))
                .statusCode(httpStatusCode.value())
                .timeStamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(httpStatusCode)
                .body(errorResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.warn("An Unhandled Exception has occurred: ", exception);

        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(new Error(UNKNOWN_ERROR)))
                .statusCode(internalServerError.value())
                .timeStamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(internalServerError)
                .body(errorResponse);
    }

}
