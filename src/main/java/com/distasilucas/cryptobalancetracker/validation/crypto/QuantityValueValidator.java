package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.Constants.INVALID_CRYPTO_QUANTITY;
import static com.distasilucas.cryptobalancetracker.constant.Constants.MAX_CRYPTO_QUANTITY;
import static com.distasilucas.cryptobalancetracker.constant.Constants.QUANTITY_FRACTIONAL_MAX_LENGTH;
import static com.distasilucas.cryptobalancetracker.constant.Constants.QUANTITY_WHOLE_MAX_LENGTH;

@Component
public class QuantityValueValidator implements EntityValidation<CryptoDTO> {

    @Override
    public void validate(CryptoDTO cryptoDTO) {
        if (!isValid(cryptoDTO.getQuantity())) {
            throw new ApiValidationException(INVALID_CRYPTO_QUANTITY);
        }
    }

    private boolean isValid(BigDecimal quantity) {
        return isGreaterThanZero(quantity) &&
                isLessThanMaxAllowed(quantity) &&
                validateLength(quantity);
    }

    private boolean isGreaterThanZero(BigDecimal quantity) {
        return quantity.compareTo(BigDecimal.valueOf(0)) > 0;
    }

    private boolean isLessThanMaxAllowed(BigDecimal quantity) {
        return quantity.compareTo(MAX_CRYPTO_QUANTITY) < 0;
    }

    private boolean validateLength(BigDecimal quantity) {
        String[] quantityValue = String.valueOf(quantity).split("\\.");

        return (quantityValue.length == 1) ||
                quantityValue[0].length() <= QUANTITY_WHOLE_MAX_LENGTH &&
                        quantityValue[1].length() <= QUANTITY_FRACTIONAL_MAX_LENGTH;
    }
}