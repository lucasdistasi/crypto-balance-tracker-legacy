package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;

@Component
public class PlatformNameValidator<T> implements EntityValidation<T> {

    @Override
    public void validate(T input) {
        String platformNameRegexValidator = "^[a-zA-Z]{1,24}$";
        String platformName = "";

        if (input instanceof CryptoRequest cryptoRequest)
            platformName = cryptoRequest.getPlatform();

        if (input instanceof PlatformRequest platformRequest)
            platformName = platformRequest.getName();

        if (StringUtils.isEmpty(platformName) || !platformName.matches(platformNameRegexValidator))
            throw new ApiValidationException(INVALID_PLATFORM_FORMAT);
    }
}
