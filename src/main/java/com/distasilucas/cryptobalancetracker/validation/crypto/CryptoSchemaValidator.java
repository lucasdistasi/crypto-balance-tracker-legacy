package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidationService;
import com.distasilucas.cryptobalancetracker.validation.PlatformNameValidator;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CryptoSchemaValidator {

    @Bean
    public Validation<CryptoRequest> addCryptoValidation(Schema addCryptoJsonSchemaValidator,
                                                         QuantityValueValidator quantityValueValidator,
                                                         PlatformNameValidator<CryptoRequest> platformNameValidator,
                                                         CryptoPlatformValidator cryptoPlatformValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(addCryptoJsonSchemaValidator),
                quantityValueValidator,
                platformNameValidator,
                cryptoPlatformValidator
        );
    }

    @Bean
    public Validation<CryptoRequest> updateCryptoValidation(Schema updateCryptoJsonSchemaValidator,
                                                            QuantityValueValidator quantityValueValidator,
                                                            PlatformNameValidator<CryptoRequest> platformNameValidator,
                                                            CryptoPlatformValidator cryptoPlatformValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(updateCryptoJsonSchemaValidator),
                quantityValueValidator,
                platformNameValidator,
                cryptoPlatformValidator
        );
    }
}
