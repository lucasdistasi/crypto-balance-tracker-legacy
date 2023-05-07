package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilValidationsTest {

    UtilValidations utilValidations = new UtilValidations();

    @ParameterizedTest
    @ValueSource(strings = {
            "a1B2C3A1b2C3A1B2c3A1B2C3", "123456789012345678901234", "ABCDEFGIJKLMNOPQRSTUVWXY"
    })
    void shouldValidateCryptoIDSuccessfully(String cryptoId) {
        utilValidations.validateCryptoIdFormat(cryptoId);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", " ", "ABCDEFGIJKLMNOPQRSTUVWX$", "$BCDEFGIJKLMNOPQRSTUVWXY", "X", "0", "ABCDEFGIJKLMNOPQRSTUVWXYZ",
            "ABCDEFGIJKLMNOPQRSTUVWX"
    })
    void shouldThrowExceptionWhenValidateCryptoID(String cryptoId) {
        var exception = assertThrows(ApiValidationException.class,
                () -> utilValidations.validateCryptoIdFormat(cryptoId));

        assertEquals("Invalid crypto ID format", exception.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "A", "ABCDEFGIJKLMNOPQRSTUVWXY"
    })
    void shouldValidatePlatformNameSuccessfully(String platformName) {
        utilValidations.validatePlatformNameFormat(platformName);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", " ", "ABCDEFGIJKLMNOPQRSTUVWX$", "$BCDEFGIJKLMNOPQRSTUVWXY", "ABCDEFGIJKLMNOPQRSTUVWXYZ", "A B",
            " AB", "AB "
    })
    void shouldThrowExceptionWhenValidatePlatformName(String platformName) {
        var exception = assertThrows(ApiValidationException.class,
                () -> utilValidations.validatePlatformNameFormat(platformName));

        assertEquals(INVALID_PLATFORM_FORMAT, exception.getErrorMessage());
    }
}