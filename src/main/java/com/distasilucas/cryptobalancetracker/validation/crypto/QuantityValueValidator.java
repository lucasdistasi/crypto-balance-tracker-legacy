package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_CRYPTO_QUANTITY;
import static com.distasilucas.cryptobalancetracker.constant.Constants.MAX_CRYPTO_QUANTITY;

@Component
public class QuantityValueValidator implements EntityValidation<CryptoRequest> {

    @Override
    public void validate(CryptoRequest cryptoRequest) {
        if (!isValid(cryptoRequest.quantity())) {
            throw new ApiValidationException(INVALID_CRYPTO_QUANTITY);
        }
    }

    private boolean isValid(BigDecimal quantity) {
        return isGreaterThanZero(quantity) &&
                isLessThanMaxAllowed(quantity) &&
                isValidLength(quantity);
    }

    private boolean isGreaterThanZero(BigDecimal quantity) {
        return quantity.compareTo(BigDecimal.valueOf(0)) > 0;
    }

    private boolean isLessThanMaxAllowed(BigDecimal quantity) {
        return quantity.compareTo(MAX_CRYPTO_QUANTITY) < 0;
    }

    private boolean isValidLength(BigDecimal quantity) {
        String[] quantityValue = String.valueOf(quantity).split("\\.");

        return quantityValue.length == 1 || isValidNumberWithDecimals(quantityValue);
    }

    private static boolean isValidNumberWithDecimals(String[] quantityValue) {
        return quantityValue[0].length() <= 16 && quantityValue[1].length() <= 12;
    }
}