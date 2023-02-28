package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.Constants.INVALID_PLATFORM_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformNameValidatorTest {

    EntityValidation<Object> entityValidation = new PlatformNameValidator<>();

    @Test
    void shouldValidateCryptoDTOSuccessfully() {
        var cryptoDTO = CryptoDTO.builder()
                .platform("Trezor")
                .build();

        entityValidation.validate(cryptoDTO);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "1234", "@/!", "ABC123"})
    void shouldThrowApiValidationExceptionWhenInvalidPlatformForCrypto(String platform) {
        var cryptoDTO = new CryptoDTO("bitcoin", BigDecimal.valueOf(0.1), platform, "btc", "bitcoin");

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(cryptoDTO));

        assertEquals(INVALID_PLATFORM_FORMAT, apiValidationException.getErrorMessage());
    }

    @Test
    void shouldValidatePlatformDTOSuccessfully() {
        var platformDTO = new PlatformDTO("Ledger");

        entityValidation.validate(platformDTO);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "1234", "@/!", "ABC123"})
    void shouldThrowApiValidationExceptionWhenInvalidPlatform(String platform) {
        var platformDTO = new PlatformDTO(platform);

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(platformDTO));

        assertEquals(INVALID_PLATFORM_FORMAT, apiValidationException.getErrorMessage());
    }

}