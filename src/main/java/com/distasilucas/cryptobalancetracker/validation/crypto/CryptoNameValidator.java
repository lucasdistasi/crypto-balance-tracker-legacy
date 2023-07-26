package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.springframework.stereotype.Service;

import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.CRYPTO_NAME_REGEX_VALIDATION;

@Service
public class CryptoNameValidator implements EntityValidation<AddCryptoRequest> {

    @Override
    public void validate(AddCryptoRequest cryptoRequest) {
        String coinName = cryptoRequest.getCryptoName();

        if (!coinName.matches(CRYPTO_NAME_REGEX_VALIDATION)) {
            throw new ApiValidationException("Invalid crypto name");
        }
    }
}
