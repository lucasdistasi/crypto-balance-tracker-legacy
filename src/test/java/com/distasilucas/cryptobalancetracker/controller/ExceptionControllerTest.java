package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.DuplicatedPlatformCoinException;
import com.distasilucas.cryptobalancetracker.exception.GoalDuplicatedException;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.io.IOException;
import java.util.Collections;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionControllerTest {

    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private static final int BAD_REQUEST_VALUE = BAD_REQUEST.value();

    ExceptionController exceptionController = new ExceptionController();

    @Test
    void shouldHandleCoinNotFoundException() {
        var coinNotFoundException = new CoinNotFoundException(COIN_NOT_FOUND);
        var responseEntity = exceptionController.handleCoinNotFoundException(coinNotFoundException);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals(coinNotFoundException.getErrorMessage(), responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandlePlatformNotFoundException() {
        var platformNotFoundException = new PlatformNotFoundException("Platform not found");
        var responseEntity = exceptionController.handlePlatformNotFoundException(platformNotFoundException);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals(platformNotFoundException.getErrorMessage(), responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleGoalNotFoundException() {
        var goalNotFoundException = new GoalNotFoundException("Goal not found");
        var responseEntity = exceptionController.handleGoalNotFoundException(goalNotFoundException);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals(goalNotFoundException.getErrorMessage(), responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleGoalDuplicatedException() {
        var goalDuplicated = new GoalDuplicatedException("Goal duplicated");
        var responseEntity = exceptionController.handleGoalDuplicatedException(goalDuplicated);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals(goalDuplicated.getErrorMessage(), responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleApiValidationException() {
        var schema = new ObjectSchema(new ObjectSchema.Builder());
        var validationException = new ValidationException(schema, "message", "keyword", "schemaLocation");
        var apiValidationException = new ApiValidationException(
                Collections.singletonList(validationException), "errorMessage", "message"
        );

        var responseEntity = exceptionController.handleApiValidationException(apiValidationException);
        var responseEntityBody = responseEntity.getBody();

        assertNotNull(responseEntityBody);
        assertAll(
                () -> assertEquals(BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(BAD_REQUEST_VALUE, responseEntityBody.statusCode()),
                () -> assertEquals(1, responseEntityBody.errors().size()),
                () -> assertEquals(validationException.getErrorMessage(), responseEntityBody.errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleApiValidationExceptionWithEmptyCausingExceptions() {
        var apiValidationException = new ApiValidationException(Collections.emptyList(), "errorMessage", "message");

        var responseEntity = exceptionController.handleApiValidationException(apiValidationException);
        var responseEntityBody = responseEntity.getBody();

        assertNotNull(responseEntityBody);
        assertAll(
                () -> assertEquals(BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(BAD_REQUEST_VALUE, responseEntityBody.statusCode()),
                () -> assertEquals(1, responseEntityBody.errors().size()),
                () -> assertEquals(apiValidationException.getMessage(), responseEntityBody.errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleApiValidationExceptionWithNullErrorMessage() {
        var schema = new ObjectSchema(new ObjectSchema.Builder());
        var validationException = new ValidationException(schema, "message", "keyword", "schemaLocation");
        var apiValidationException = new ApiValidationException(
                Collections.singletonList(validationException), null, "message"
        );

        var responseEntity = exceptionController.handleApiValidationException(apiValidationException);
        var responseEntityBody = responseEntity.getBody();

        assertNotNull(responseEntityBody);
        assertAll(
                () -> assertEquals(BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(BAD_REQUEST_VALUE, responseEntityBody.statusCode()),
                () -> assertEquals(1, responseEntityBody.errors().size()),
                () -> assertEquals(validationException.getErrorMessage(), responseEntityBody.errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleHttpMessageNotReadableExceptionWithRootInvalidFormatException() throws IOException {
        try (var jsonParser = new JsonFactory().createParser("content")) {
            var invalidFormatException = new InvalidFormatException(jsonParser, "InvalidFormatException_Error", new Object(), String.class);
            var mockClientHttpResponse = new MockClientHttpResponse(new byte[0], HttpStatus.BAD_REQUEST);
            var httpMessageNotReadableException = new HttpMessageNotReadableException("", invalidFormatException, mockClientHttpResponse);

            var responseEntity = exceptionController.handleHttpMessageNotReadableException(httpMessageNotReadableException);
            var responseEntityBody = responseEntity.getBody();

            assertNotNull(responseEntityBody);
            assertAll(
                    () -> assertEquals(BAD_REQUEST, responseEntity.getStatusCode()),
                    () -> assertEquals(BAD_REQUEST_VALUE, responseEntityBody.statusCode()),
                    () -> assertEquals(1, responseEntityBody.errors().size()),
                    () -> assertEquals(invalidFormatException.getOriginalMessage(), responseEntityBody.errors().get(0).errorMessage())
            );
        }
    }

    @Test
    void shouldHandleHttpMessageNotReadableExceptionWithRootJsonParseException() throws IOException {
        try (var jsonParser = new JsonFactory().createParser("content")) {
            var invalidFormatException = new JsonParseException(jsonParser, "JsonParseException");
            var mockClientHttpResponse = new MockClientHttpResponse(new byte[0], HttpStatus.BAD_REQUEST);
            var httpMessageNotReadableException = new HttpMessageNotReadableException("", invalidFormatException, mockClientHttpResponse);

            var responseEntity = exceptionController.handleHttpMessageNotReadableException(httpMessageNotReadableException);
            var responseEntityBody = responseEntity.getBody();

            assertNotNull(responseEntityBody);
            assertAll(
                    () -> assertEquals(BAD_REQUEST, responseEntity.getStatusCode()),
                    () -> assertEquals(BAD_REQUEST_VALUE, responseEntityBody.statusCode()),
                    () -> assertEquals(1, responseEntityBody.errors().size()),
                    () -> assertEquals(invalidFormatException.getOriginalMessage(), responseEntityBody.errors().get(0).errorMessage())
            );
        }
    }

    @Test
    void shouldHandleDuplicatedPlatformCoinException() {
        var duplicatedPlatformCoinException = new DuplicatedPlatformCoinException("Duplicated platform");
        var responseEntity = exceptionController.handleDuplicatedPlatformCoinException(duplicatedPlatformCoinException);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals(duplicatedPlatformCoinException.getErrorMessage(), responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleApiException() {
        var apiException = new ApiException("You've found the easter egg", HttpStatus.I_AM_A_TEAPOT);

        var responseEntity = exceptionController.handleApiException(apiException);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals(apiException.getErrorMessage(), responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleMissingServletRequestParameterException() {
        var exception = new MissingServletRequestParameterException("parameter", "string");

        var responseEntity = exceptionController.handleMissingServletRequestParameterException(exception);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals("Required parameter 'parameter' is not present.", responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        var exception = new IllegalArgumentException("IllegalArgumentException");

        var responseEntity = exceptionController.handleIllegalArgumentException(exception);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().errors().size()),
                () -> assertEquals("IllegalArgumentException", responseEntity.getBody().errors().get(0).errorMessage())
        );
    }

    @Test
    void shouldHandleException() {
        var responseEntity = exceptionController.handleException(new Exception());
        var responseEntityBody = responseEntity.getBody();

        assertNotNull(responseEntityBody);
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode()),
                () -> assertEquals(responseEntityBody.statusCode(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
                () -> assertEquals(1, responseEntityBody.errors().size()),
                () -> assertEquals("Unknown Error", responseEntityBody.errors().get(0).errorMessage())
        );

    }
}