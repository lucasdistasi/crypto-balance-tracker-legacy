package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionControllerTest {

    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private static final int BAD_REQUEST_VALUE = BAD_REQUEST.value();

    ExceptionController exceptionController = new ExceptionController();

    @Test
    void shouldHandleApiValidationException() {
        var schema = new ObjectSchema(new ObjectSchema.Builder());
        var validationException = new ValidationException(schema, "message", "keyword", "schemaLocation");
        var apiValidationException = new ApiValidationException(
                Collections.singletonList(validationException), "ApiValidationException"
        );

        var responseEntity = exceptionController.handleApiValidationException(apiValidationException);
        var responseEntityBody = responseEntity.getBody();

        assertNotNull(responseEntityBody);
        assertAll("apiValidationException",
                () -> assertEquals(responseEntity.getStatusCode(), BAD_REQUEST),
                () -> assertEquals(responseEntityBody.getStatusCode(), BAD_REQUEST_VALUE),
                () -> assertEquals(responseEntityBody.getErrors().size(), 1),
                () -> assertEquals(responseEntityBody.getErrors().get(0).errorMessage(), validationException.getErrorMessage())
        );
    }

    @Test
    void shouldHandleApiValidationExceptionWithEmptyCausingExceptions() {
        var apiValidationException = new ApiValidationException(
                Collections.emptyList(), "ApiValidationException"
        );

        var responseEntity = exceptionController.handleApiValidationException(apiValidationException);
        var responseEntityBody = responseEntity.getBody();

        assertNotNull(responseEntityBody);
        assertAll("apiValidationException",
                () -> assertEquals(responseEntity.getStatusCode(), BAD_REQUEST),
                () -> assertEquals(responseEntityBody.getStatusCode(), BAD_REQUEST_VALUE),
                () -> assertEquals(responseEntityBody.getErrors().size(), 1),
                () -> assertEquals(responseEntityBody.getErrors().get(0).errorMessage(), apiValidationException.getErrorMessage())
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
            assertAll("apiValidationException",
                    () -> assertEquals(responseEntity.getStatusCode(), BAD_REQUEST),
                    () -> assertEquals(responseEntityBody.getStatusCode(), BAD_REQUEST_VALUE),
                    () -> assertEquals(responseEntityBody.getErrors().size(), 1),
                    () -> assertEquals(responseEntityBody.getErrors().get(0).errorMessage(), invalidFormatException.getOriginalMessage())
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
            assertAll("apiValidationException",
                    () -> assertEquals(responseEntity.getStatusCode(), BAD_REQUEST),
                    () -> assertEquals(responseEntityBody.getStatusCode(), BAD_REQUEST_VALUE),
                    () -> assertEquals(responseEntityBody.getErrors().size(), 1),
                    () -> assertEquals(responseEntityBody.getErrors().get(0).errorMessage(), invalidFormatException.getOriginalMessage())
            );
        }
    }

    @Test
    void shouldHandleException() {
        var responseEntity = exceptionController.handleException(new Exception());
        var responseEntityBody = responseEntity.getBody();

        assertNotNull(responseEntityBody);
        assertAll("apiValidationException",
                () -> assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR),
                () -> assertEquals(responseEntityBody.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
                () -> assertEquals(responseEntityBody.getErrors().size(), 1),
                () -> assertEquals(responseEntityBody.getErrors().get(0).errorMessage(), "Unknown Error")
        );

    }
}