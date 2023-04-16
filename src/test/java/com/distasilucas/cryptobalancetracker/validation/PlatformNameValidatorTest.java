package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformNameValidatorTest {

    EntityValidation<Object> entityValidation = new PlatformNameValidator<>();

    @Test
    void shouldValidateCryptoRequestSuccessfully() {
        var cryptoRequest = new CryptoRequest("bitcoin", BigDecimal.valueOf(0.1), "Trezor");

        entityValidation.validate(cryptoRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "1234", "@/!", "ABC123"})
    void shouldThrowApiValidationExceptionWhenInvalidPlatformForCrypto(String platform) {
        var cryptoRequest = new CryptoRequest("bitcoin", BigDecimal.valueOf(0.1), platform);

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(cryptoRequest));

        assertEquals(INVALID_PLATFORM_FORMAT, apiValidationException.getErrorMessage());
    }

    @Test
    void shouldValidatePlatformRequestSuccessfully() {
        var platformRequest = new PlatformRequest("Ledger");

        entityValidation.validate(platformRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "1234", "@/!", "ABC123"})
    void shouldThrowApiValidationExceptionWhenInvalidPlatform(String platform) {
        var platformRequest = new PlatformRequest(platform);

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(platformRequest));

        assertEquals(INVALID_PLATFORM_FORMAT, apiValidationException.getErrorMessage());
    }

}