package com.distasilucas.cryptobalancetracker.validation;

import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.validation.crypto.QuantityValueValidator;
import lombok.RequiredArgsConstructor;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchemaValidator {

    @Bean
    public Validation<CryptoDTO> addCryptoValidation(Schema addCryptoJsonSchemaValidator,
                                                     QuantityValueValidator quantityValueValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(addCryptoJsonSchemaValidator),
                quantityValueValidator
        );
    }

    @Bean
    public Validation<CryptoDTO> updateCryptoValidation(Schema updateCryptoJsonSchemaValidator,
                                                     QuantityValueValidator quantityValueValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(updateCryptoJsonSchemaValidator),
                quantityValueValidator
        );
    }
}
