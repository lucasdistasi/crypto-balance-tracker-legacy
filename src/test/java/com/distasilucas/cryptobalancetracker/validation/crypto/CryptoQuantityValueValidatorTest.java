package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.model.request.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import com.distasilucas.cryptobalancetracker.validation.QuantityValueValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CryptoQuantityValueValidatorTest {

    @Mock
    QuantityValueValidator quantityValueValidatorMock;

    EntityValidation<AddCryptoRequest> entityValidation;

    @BeforeEach
    void setUp() {
        entityValidation = new CryptoQuantityValueValidator<>(quantityValueValidatorMock);
    }

    @Test
    void shouldValidateQuantitySuccessfully() {
        var addCryptoRequest = new AddCryptoRequest("bitcoin", BigDecimal.ONE, "Binance");

        entityValidation.validate(addCryptoRequest);

        verify(quantityValueValidatorMock, times(1)).validateCryptoQuantity(addCryptoRequest.getQuantity());
    }
}