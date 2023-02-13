package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.distasilucas.cryptobalancetracker.constant.Constants.INVALID_PLATFORM_FORMAT;

@Component
public class PlatformNameValidator<T> implements EntityValidation<T> {

    @Override
    public void validate(T input) {
        String platformNameRegexValidator = "^[a-zA-Z]+$";
        String platformName = "";

        if (input instanceof CryptoDTO cryptoDTO)
            platformName = cryptoDTO.getPlatform();

        if (input instanceof PlatformDTO platformDTO)
            platformName = platformDTO.getName();

        if (StringUtils.isNotEmpty(platformName) && !platformName.matches(platformNameRegexValidator))
            throw new ApiValidationException(INVALID_PLATFORM_FORMAT);
    }
}
