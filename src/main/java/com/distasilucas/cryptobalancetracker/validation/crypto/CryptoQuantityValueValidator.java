package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import com.distasilucas.cryptobalancetracker.validation.QuantityValueValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CryptoQuantityValueValidator<T extends CryptoRequest> implements EntityValidation<T> {

    private final QuantityValueValidator quantityValueValidator;

    @Override
    public void validate(T cryptoRequest) {
        quantityValueValidator.validateCryptoQuantity(cryptoRequest.getQuantity());
    }
}