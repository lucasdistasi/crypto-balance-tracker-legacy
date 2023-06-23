package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_ID_MONGO_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CryptoIdValidatorTest {

    EntityValidation<UpdateCryptoRequest> cryptoIdValidator = new CryptoIdValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "a1B2C3A1b2C3A1B2c3A1B2C3", "123456789012345678901234", "ABCDEFGIJKLMNOPQRSTUVWXY"
    })
    void shouldValidateCryptoId(String cryptoId) {
        var updateCryptoRequest = new UpdateCryptoRequest(cryptoId, BigDecimal.ONE, "Binance");

        cryptoIdValidator.validate(updateCryptoRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", " ", "ABCDEFGIJKLMNOPQRSTUVWX$", "$BCDEFGIJKLMNOPQRSTUVWXY", "X", "0", "ABCDEFGIJKLMNOPQRSTUVWXYZ",
            "ABCDEFGIJKLMNOPQRSTUVWX"
    })
    void shouldThrowExceptionWhenValidateCryptoID(String cryptoId) {
        var updateCryptoRequest = new UpdateCryptoRequest(cryptoId, BigDecimal.ONE, "Binance");

        var exception = assertThrows(ApiValidationException.class,
                () -> cryptoIdValidator.validate(updateCryptoRequest));

        assertEquals(INVALID_ID_MONGO_FORMAT, exception.getErrorMessage());
    }

}