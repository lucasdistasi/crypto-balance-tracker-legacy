package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.PLATFORM_NAME_REGEX_VALIDATION;

@Component
public class PlatformNameValidator<T> implements EntityValidation<T> {

    @Override
    public void validate(T input) {
        String platformName = "";

        if (input instanceof CryptoRequest cryptoRequest)
            platformName = cryptoRequest.getPlatform();

        if (input instanceof PlatformRequest platformRequest)
            platformName = platformRequest.getName();

        if (StringUtils.isEmpty(platformName) || !platformName.matches(PLATFORM_NAME_REGEX_VALIDATION))
            throw new ApiValidationException(INVALID_PLATFORM_FORMAT);
    }
}
