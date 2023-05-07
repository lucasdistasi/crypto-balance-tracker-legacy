package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import org.springframework.stereotype.Component;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_CRYPTO_ID_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.CRYPTO_ID_REGEX_VALIDATION;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.PLATFORM_NAME_REGEX_VALIDATION;

@Component
public class UtilValidations {

    public void validateCryptoIdFormat(String cryptoId) {
        if (!cryptoId.matches(CRYPTO_ID_REGEX_VALIDATION))
            throw new ApiValidationException(INVALID_CRYPTO_ID_FORMAT);
    }

    public void validatePlatformNameFormat(String platformName) {
        if (!platformName.matches(PLATFORM_NAME_REGEX_VALIDATION))
            throw new ApiValidationException(INVALID_PLATFORM_FORMAT);
    }
}
