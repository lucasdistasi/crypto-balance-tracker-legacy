package com.distasilucas.cryptobalancetracker.validation.platform;

import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidationService;
import com.distasilucas.cryptobalancetracker.validation.PlatformNameValidator;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PlatformSchemaValidator {

    @Bean
    public Validation<PlatformDTO> addPlatformValidation(Schema addPlatformJsonSchemaValidator,
                                                         PlatformNameValidator<PlatformDTO> platformNameValidator,
                                                         PlatformNotExistsValidator platformNotExistsValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(addPlatformJsonSchemaValidator),
                platformNameValidator,
                platformNotExistsValidator
        );
    }
}
