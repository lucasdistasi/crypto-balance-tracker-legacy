package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.Constants.INVALID_CRYPTO_QUANTITY;
import static org.junit.jupiter.api.Assertions.*;

class QuantityValueValidatorTest {

    EntityValidation<CryptoDTO> entityValidation = new QuantityValueValidator();

    @Test
    void shouldValidateQuantitySuccessfully() {
        var cryptoDTO = getCryptoDTO(BigDecimal.valueOf(123.456));

        try {
            entityValidation.validate(cryptoDTO);
        } catch (Exception ex) {
            fail("Should not fail");
        }
    }

    @Test
    void shouldValidateQuantitySuccessfullyWithoutDecimals() {
        var cryptoDTO = getCryptoDTO(BigDecimal.valueOf(123));

        try {
            entityValidation.validate(cryptoDTO);
        } catch (Exception ex) {
            fail("Should not fail");
        }
    }

    @Test
    void shouldThrowApiValidationExceptionIfLengthIsInvalid() {
        var cryptoDTO = getCryptoDTO(BigDecimal.valueOf(9999999999999999.9999999999999));

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> entityValidation.validate(cryptoDTO)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 0})
    void shouldThrowApiValidationExceptionIfQuantityIsNotGreaterThanZero(double quantity) {
        var cryptoDTO = getCryptoDTO(BigDecimal.valueOf(quantity));

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> entityValidation.validate(cryptoDTO)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    @Test
    void shouldThrowApiValidationExceptionIfQuantityIsGreaterThanAllowed() {
        var cryptoDTO = getCryptoDTO(BigDecimal.valueOf(9999999999999999.99));

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> entityValidation.validate(cryptoDTO)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    private CryptoDTO getCryptoDTO(BigDecimal quantity) {
        return CryptoDTO.builder()
                .quantity(quantity)
                .build();
    }

}