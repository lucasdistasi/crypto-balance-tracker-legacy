package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.distasilucas.cryptobalancetracker.constant.Constants.ERROR_VALIDATING_JSON_SCHEMA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class JsonSchemaValidationServiceTest {

    @Mock
    Schema schemaMock;

    EntityValidation<CryptoDTO> cryptoDTOEntityValidation;

    @BeforeEach
    void setUp() {
        cryptoDTOEntityValidation = new JsonSchemaValidationService<>(schemaMock);
    }

    @Test
    void shouldValidateJsonSchemaSuccessfully() {
        var cryptoDTO = CryptoDTO.builder()
                .build();

        try {
            cryptoDTOEntityValidation.validate(cryptoDTO);
        } catch (Exception ex) {
            fail("Should not fail");
        }
    }

    @Test
    void shouldThrowApiValidationExceptionIfJsonSchemaIsInvalid() {
        var cryptoDTO = CryptoDTO.builder()
                .build();
        var validationException = new ValidationException(schemaMock, "required key [name] not found", "keyword", "schemaLocation");

        doThrow(validationException).when(schemaMock).validate(any());

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> cryptoDTOEntityValidation.validate(cryptoDTO)
        );

        var expectedMessage = String.format(ERROR_VALIDATING_JSON_SCHEMA, validationException.getMessage());
        assertEquals(apiValidationException.getMessage(), expectedMessage);
    }

}