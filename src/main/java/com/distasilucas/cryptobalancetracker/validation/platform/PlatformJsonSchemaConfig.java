package com.distasilucas.cryptobalancetracker.validation.platform;

import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidator;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformJsonSchemaConfig implements JsonSchemaValidator {

    @Bean
    public Schema addPlatformJsonSchemaValidator() {
        return validateJsonSchema(getClass().getResourceAsStream("/schemas/platform/addPlatformSchema.json"));
    }
}
