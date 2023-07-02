package com.distasilucas.cryptobalancetracker.validation.goal;

import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidator;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoalJsonSchemaConfig implements JsonSchemaValidator {

    @Bean
    public Schema addGoalJsonSchemaValidator() {
        return validateJsonSchema(getClass().getResourceAsStream("/schemas/goal/addGoalSchema.json"));
    }

    @Bean
    public Schema updateGoalJsonSchemaValidator() {
        return validateJsonSchema(getClass().getResourceAsStream("/schemas/goal/updateGoalSchema.json"));
    }
}
