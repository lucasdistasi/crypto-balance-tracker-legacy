package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.InsufficientBalanceException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import com.distasilucas.cryptobalancetracker.validation.QuantityValueValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_FEE_QUANTITY;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.NETWORK_FEE_HIGHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TransferCryptoValidatorTest {

    @Mock
    QuantityValueValidator quantityValueValidatorMock;

    EntityValidation<TransferCryptoRequest> entityValidation;

    @BeforeEach
    void setUp() {
        entityValidation = new TransferCryptoValidator(quantityValueValidatorMock);
    }

    @Test
    void shouldValidateSuccessfully() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(0.5),
                BigDecimal.valueOf(0.001),
                "Binance"
        );

        entityValidation.validate(transferCryptoRequest);
    }

    @Test
    void shouldThrowInsufficientBalanceException() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(0.5),
                BigDecimal.valueOf(1),
                "Binance"
        );

        var exception = assertThrows(InsufficientBalanceException.class,
                () -> entityValidation.validate(transferCryptoRequest));

        assertEquals(NETWORK_FEE_HIGHER, exception.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ab,", " a", "$a", "12", "abcabcabcabcabcabcabcabca"
    })
    void shouldThrowApiValidationExceptionWhenInvalidPlatform(String fromPlatform) {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(0.001),
                fromPlatform
        );

        var exception = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(transferCryptoRequest));

        assertEquals(INVALID_PLATFORM_FORMAT, exception.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "9999999999999999.9999999999999",
            "99999999999999999.999999999999"})
    void shouldThrowApiValidationExceptionWhenInvalidNetworkFee(String networkFee) {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(0.5),
                new BigDecimal(networkFee),
                "Binance"
        );

        var exception = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(transferCryptoRequest));

        assertEquals(INVALID_FEE_QUANTITY, exception.getErrorMessage());
    }

}