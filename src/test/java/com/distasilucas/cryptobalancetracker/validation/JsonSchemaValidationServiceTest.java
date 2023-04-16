package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.ERROR_VALIDATING_JSON_SCHEMA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class JsonSchemaValidationServiceTest {

    @Mock
    Schema schemaMock;

    EntityValidation<CryptoRequest> cryptoRequestEntityValidation;

    @BeforeEach
    void setUp() {
        cryptoRequestEntityValidation = new JsonSchemaValidationService<>(schemaMock);
    }

    @Test
    void shouldValidateJsonSchemaSuccessfully() {
        var cryptoRequest = new CryptoRequest("Ethereum", BigDecimal.valueOf(1), "Ledger");

        cryptoRequestEntityValidation.validate(cryptoRequest);
    }

    @Test
    void shouldThrowApiValidationExceptionIfJsonSchemaIsInvalid() {
        var cryptoRequest = new CryptoRequest("Ethereum", BigDecimal.valueOf(1), "Ledger");
        var validationException = new ValidationException(schemaMock, "required key [name] not found", "keyword", "schemaLocation");

        doThrow(validationException).when(schemaMock).validate(any());

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> cryptoRequestEntityValidation.validate(cryptoRequest)
        );

        var expectedMessage = String.format(ERROR_VALIDATING_JSON_SCHEMA, validationException.getMessage());

        assertEquals(apiValidationException.getMessage(), expectedMessage);
    }

}