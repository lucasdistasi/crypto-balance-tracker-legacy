package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.model.request.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidationService;
import com.distasilucas.cryptobalancetracker.validation.PlatformNameValidator;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CryptoSchemaValidator {

    @Bean
    public Validation<AddCryptoRequest> addCryptoValidation(Schema addCryptoJsonSchemaValidator,
                                                            QuantityValueValidator<AddCryptoRequest> quantityValueValidator,
                                                            PlatformNameValidator<AddCryptoRequest> platformNameValidator,
                                                            CryptoPlatformValidator<AddCryptoRequest> cryptoPlatformValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(addCryptoJsonSchemaValidator),
                quantityValueValidator,
                platformNameValidator,
                cryptoPlatformValidator
        );
    }

    @Bean
    public Validation<UpdateCryptoRequest> updateCryptoValidation(Schema updateCryptoJsonSchemaValidator,
                                                                  QuantityValueValidator<UpdateCryptoRequest> quantityValueValidator,
                                                                  PlatformNameValidator<UpdateCryptoRequest> platformNameValidator,
                                                                  CryptoPlatformValidator<UpdateCryptoRequest> cryptoPlatformValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(updateCryptoJsonSchemaValidator),
                quantityValueValidator,
                platformNameValidator,
                cryptoPlatformValidator
        );
    }
}
