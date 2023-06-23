package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import org.springframework.stereotype.Component;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_ID_MONGO_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_PLATFORM_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.ID_MONGO_REGEX_VALIDATION;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.PLATFORM_NAME_REGEX_VALIDATION;

@Component
public class UtilValidations {

    public void validateIdMongoEntityFormat(String mongoEntityId) {
        if (!mongoEntityId.matches(ID_MONGO_REGEX_VALIDATION))
            throw new ApiValidationException(INVALID_ID_MONGO_FORMAT);
    }

    public void validatePlatformNameFormat(String platformName) {
        if (!platformName.matches(PLATFORM_NAME_REGEX_VALIDATION))
            throw new ApiValidationException(INVALID_PLATFORM_FORMAT);
    }
}
