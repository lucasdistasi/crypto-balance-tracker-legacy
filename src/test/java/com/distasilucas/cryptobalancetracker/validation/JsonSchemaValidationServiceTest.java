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

import static org.junit.jupiter.api.Assertions.*;
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
    void shouldThrowApiValidationExceptionIfJsonSchemaIsinvalid() {
        var cryptoDTO = CryptoDTO.builder()
                .build();

        doThrow(ValidationException.class).when(schemaMock).validate(any());

        var validationException = assertThrows(ApiValidationException.class,
                () -> cryptoDTOEntityValidation.validate(cryptoDTO)
        );

        assertEquals(validationException.getMessage(), "Error validating Json Schema");
    }

}