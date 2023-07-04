package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_CRYPTO_QUANTITY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuantityValueValidatorTest {

    QuantityValueValidator quantityValueValidator = new QuantityValueValidator();

    @Test
    void shouldValidateQuantitySuccessfully() {
        quantityValueValidator.validateCryptoQuantity(BigDecimal.valueOf(123.456));
    }

    @Test
    void shouldValidateQuantitySuccessfullyWithoutDecimals() {
        quantityValueValidator.validateCryptoQuantity(BigDecimal.valueOf(123));
    }

    @ParameterizedTest
    @ValueSource(strings = { "9999999999999999.9999999999999",
            "99999999999999999.999999999999"})
    void shouldThrowApiValidationExceptionIfLengthIsInvalid(String length) {
        var quantity = new BigDecimal(length);

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> quantityValueValidator.validateCryptoQuantity(quantity)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 0})
    void shouldThrowApiValidationExceptionIfQuantityIsNotGreaterThanZero(double quantity) {
        var quantityToValidate = BigDecimal.valueOf(quantity);

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> quantityValueValidator.validateCryptoQuantity(quantityToValidate)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    @Test
    void shouldThrowApiValidationExceptionIfQuantityIsGreaterThanAllowed() {
        var quantityToValidate = BigDecimal.valueOf(9999999999999999.99);

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> quantityValueValidator.validateCryptoQuantity(quantityToValidate)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

}