package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.springframework.stereotype.Service;

@Service
public class CryptoNameValidator implements EntityValidation<AddCryptoRequest> {

    @Override
    public void validate(AddCryptoRequest cryptoRequest) {
        String coinName = cryptoRequest.getCoinName();
        String regex = "^(?! )(?!.* {2})[a-zA-Z0-9]+(?:[ {2}][a-zA-Z0-9]+)*$(?<! )";

        if (!coinName.matches(regex)) {
            throw new ApiValidationException("Invalid crypto name");
        }
    }
}
