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
    public Validation<CryptoDTO> addCryptoValidation(Schema cryptoJsonSchemaValidator,
                                                     QuantityValueValidator quantityValueValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(cryptoJsonSchemaValidator),
                quantityValueValidator
        );
    }
}
