package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidator;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoJsonSchemaConfig implements JsonSchemaValidator {

    @Bean
    public Schema addCryptoJsonSchemaValidator() {
        return validateJsonSchema(getClass().getResourceAsStream("/schemas/crypto/addCryptoSchema.json"));
    }

    @Bean
    public Schema updateCryptoJsonSchemaValidator() {
        return validateJsonSchema(getClass().getResourceAsStream("/schemas/crypto/updateCryptoSchema.json"));
    }
}
