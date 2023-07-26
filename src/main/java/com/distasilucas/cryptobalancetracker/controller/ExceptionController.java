package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.GoalDuplicatedException;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.InsufficientBalanceException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.error.Error;
import com.distasilucas.cryptobalancetracker.model.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.everit.json.schema.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.UNKNOWN_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = CryptoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCryptoNotFoundException(CryptoNotFoundException cryptoNotFoundException) {
        log.warn("A CryptoNotFoundException has occurred: ", cryptoNotFoundException);

        Error error = new Error(cryptoNotFoundException.getErrorMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), Collections.singletonList(error));

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(value = PlatformNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlatformNotFoundException(PlatformNotFoundException platformNotFoundException) {
        log.warn("A PlatformNotFoundException has occurred: ", platformNotFoundException);

        Error error = new Error(platformNotFoundException.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), Collections.singletonList(error));

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(value = GoalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGoalNotFoundException(GoalNotFoundException goalNotFoundException) {
        log.warn("A GoalNotFoundException has occurred: ", goalNotFoundException);

        Error error = new Error(goalNotFoundException.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), Collections.singletonList(error));

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(value = InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException insufficientBalanceException) {
        log.warn("A InsufficientBalanceException has occurred: ", insufficientBalanceException);

        Error error = new Error(insufficientBalanceException.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), Collections.singletonList(error));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(value = GoalDuplicatedException.class)
    public ResponseEntity<ErrorResponse> handleGoalDuplicatedException(GoalDuplicatedException goalDuplicatedException) {
        log.warn("A GoalDuplicatedException has occurred: ", goalDuplicatedException);

        Error error = new Error(goalDuplicatedException.getErrorMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), Collections.singletonList(error));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors);

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
        log.warn("A HttpMessageNotReadableException has occurred: ", httpMessageNotReadableException);

        String originalMessage = UNKNOWN_ERROR;

        Throwable rootCause = httpMessageNotReadableException.getRootCause();
        if (rootCause instanceof InvalidFormatException invalidFormatException) {
            originalMessage = invalidFormatException.getOriginalMessage();
        }

        if (rootCause instanceof JsonParseException jsonParseException) {
            originalMessage = jsonParseException.getOriginalMessage();
        }

        Error error = new Error(originalMessage);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), Collections.singletonList(error));

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException apiException) {
        log.warn("An ApiException has occurred: ", apiException);

        HttpStatusCode httpStatusCode = apiException.getHttpStatusCode();

        Error error = new Error(apiException.getErrorMessage());
        ErrorResponse errorResponse = new ErrorResponse(httpStatusCode.value(), Collections.singletonList(error));

        return ResponseEntity.status(httpStatusCode)
                .body(errorResponse);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.warn("A MissingServletRequestParameterException has occurred: ", exception);

        Error error = new Error(exception.getBody().getDetail());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), Collections.singletonList(error));

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("An IllegalArgumentException has occurred: ", exception);

        Error error = new Error(exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), Collections.singletonList(error));

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.warn("An Unhandled Exception has occurred: ", exception);

        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        Error error = new Error(UNKNOWN_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(internalServerError.value(), Collections.singletonList(error));

        return ResponseEntity.status(internalServerError)
                .body(errorResponse);
    }

}
