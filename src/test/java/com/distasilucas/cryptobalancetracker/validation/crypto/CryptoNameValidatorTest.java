package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CryptoNameValidatorTest {

    EntityValidation<AddCryptoRequest> entityValidation = new CryptoNameValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "bitcoin", "oasis network", "1inch", "0x Protocol", "baby doge coin", "b"
    })
    void shouldValidateSuccessfully(String cryptoName) {
        var addCryptoRequest = new AddCryptoRequest(cryptoName, BigDecimal.ONE, "Binance");
        entityValidation.validate(addCryptoRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " bitcoin", "bitcoin ", "bit  coin", "$bitcoin", "bitcoin$", "bit.coin", "bit_coin", "bit-coin", "bit/coin"
    })
    void shouldThrowExceptionForInvalidCryptoName(String cryptoName) {
        var addCryptoRequest = new AddCryptoRequest(cryptoName, BigDecimal.ONE, "Binance");

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(addCryptoRequest));

        assertEquals("Invalid crypto name", apiValidationException.getMessage());
    }
}