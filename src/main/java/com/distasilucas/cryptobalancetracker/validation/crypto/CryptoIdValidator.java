package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.springframework.stereotype.Component;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_ID_MONGO_FORMAT;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.ID_MONGO_REGEX_VALIDATION;

@Component
public class CryptoIdValidator implements EntityValidation<UpdateCryptoRequest> {

    @Override
    public void validate(UpdateCryptoRequest updateCryptoRequest) {
        if (!updateCryptoRequest.getCryptoId().matches(ID_MONGO_REGEX_VALIDATION))
            throw new ApiValidationException(INVALID_ID_MONGO_FORMAT);
    }
}
