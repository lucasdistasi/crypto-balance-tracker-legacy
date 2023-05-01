package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_CRYPTO_QUANTITY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuantityValueValidatorTest {

    EntityValidation<AddCryptoRequest> entityValidation = new QuantityValueValidator();

    @Test
    void shouldValidateQuantitySuccessfully() {
        var cryptoRequest = getCryptoRequest(BigDecimal.valueOf(123.456));

        entityValidation.validate(cryptoRequest);
    }

    @Test
    void shouldValidateQuantitySuccessfullyWithoutDecimals() {
        var cryptoRequest = getCryptoRequest(BigDecimal.valueOf(123));

        entityValidation.validate(cryptoRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = { "9999999999999999.9999999999999",
            "99999999999999999.999999999999"})
    void shouldThrowApiValidationExceptionIfLengthIsInvalid(String length) {
        var quantity = new BigDecimal(length);
        var cryptoRequest = getCryptoRequest(quantity);

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> entityValidation.validate(cryptoRequest)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 0})
    void shouldThrowApiValidationExceptionIfQuantityIsNotGreaterThanZero(double quantity) {
        var cryptoRequest = getCryptoRequest(BigDecimal.valueOf(quantity));

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> entityValidation.validate(cryptoRequest)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    @Test
    void shouldThrowApiValidationExceptionIfQuantityIsGreaterThanAllowed() {
        var cryptoRequest = getCryptoRequest(BigDecimal.valueOf(9999999999999999.99));

        var apiValidationException = assertThrows(
                ApiValidationException.class,
                () -> entityValidation.validate(cryptoRequest)
        );

        assertAll(
                () -> assertEquals(INVALID_CRYPTO_QUANTITY, apiValidationException.getMessage())
        );
    }

    private AddCryptoRequest getCryptoRequest(BigDecimal quantity) {
        return new AddCryptoRequest("Bitcoin", quantity, "Safepal");
    }

}