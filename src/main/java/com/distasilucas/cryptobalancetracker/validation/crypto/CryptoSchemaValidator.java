package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidationService;
import com.distasilucas.cryptobalancetracker.validation.PlatformNameValidator;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CryptoSchemaValidator {

    @Bean
    public Validation<CryptoDTO> addCryptoValidation(Schema addCryptoJsonSchemaValidator,
                                                     QuantityValueValidator quantityValueValidator,
                                                     PlatformNameValidator<CryptoDTO> platformNameValidator,
                                                     CryptoPlatformValidator cryptoPlatformValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(addCryptoJsonSchemaValidator),
                quantityValueValidator,
                platformNameValidator,
                cryptoPlatformValidator
        );
    }

    @Bean
    public Validation<CryptoDTO> updateCryptoValidation(Schema updateCryptoJsonSchemaValidator,
                                                        QuantityValueValidator quantityValueValidator,
                                                        PlatformNameValidator<CryptoDTO> platformNameValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(updateCryptoJsonSchemaValidator),
                quantityValueValidator,
                platformNameValidator
        );
    }
}
