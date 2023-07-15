package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.InsufficientBalanceException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import com.distasilucas.cryptobalancetracker.validation.QuantityValueValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_FEE_QUANTITY;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.NETWORK_FEE_HIGHER;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.PLATFORM_NAME_REGEX_VALIDATION;

@Component
@RequiredArgsConstructor
public class TransferCryptoValidator implements EntityValidation<TransferCryptoRequest> {

    private final QuantityValueValidator quantityValueValidator;

    @Override
    public void validate(TransferCryptoRequest transferCryptoRequest) {
        validateNetworkFee(transferCryptoRequest.getNetworkFee());

        if (isNetworkFeeHigherThanQuantity(transferCryptoRequest))
            throw new InsufficientBalanceException(NETWORK_FEE_HIGHER);

        if (isInvalidPlatform(transferCryptoRequest.getToPlatform()))
            throw new ApiValidationException(INVALID_PLATFORM_FORMAT);

        quantityValueValidator.validateCryptoQuantity(transferCryptoRequest.getQuantityToTransfer());
    }

    private boolean isNetworkFeeHigherThanQuantity(TransferCryptoRequest transferCryptoRequest) {
        return transferCryptoRequest.getNetworkFee().compareTo(transferCryptoRequest.getQuantityToTransfer()) > 0;
    }

    private boolean isInvalidPlatform(String toPlatform) {
        return StringUtils.isEmpty(toPlatform) ||
                !toPlatform.matches(PLATFORM_NAME_REGEX_VALIDATION);
    }

    private void validateNetworkFee(BigDecimal networkFee) {
        String[] networkFeeValue = String.valueOf(networkFee).split("\\.");

        if (!(networkFeeValue.length == 1 || isValidNumberWithDecimals(networkFeeValue)))
            throw new ApiValidationException(INVALID_FEE_QUANTITY);
    }

    private boolean isValidNumberWithDecimals(String[] quantityValue) {
        return quantityValue[0].length() <= 16 && quantityValue[1].length() <= 12;
    }
}
