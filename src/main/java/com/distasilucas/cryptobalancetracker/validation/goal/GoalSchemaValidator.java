package com.distasilucas.cryptobalancetracker.validation.goal;

import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.validation.JsonSchemaValidationService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.everit.json.schema.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GoalSchemaValidator {

    @Bean
    public Validation<AddGoalRequest> addGoalRequestValidation(Schema addGoalJsonSchemaValidator,
                                                               ValidateGoalQuantity<AddGoalRequest> validateGoalQuantity,
                                                               GoalCryptoNameValidator goalCryptoNameValidator) {
        return new Validation<>(
                new JsonSchemaValidationService<>(addGoalJsonSchemaValidator),
                validateGoalQuantity,
                goalCryptoNameValidator
        );
    }

    @Bean
    public Validation<UpdateGoalRequest> updateGoalRequestValidation(Schema updateGoalJsonSchemaValidator,
                                                                     ValidateGoalQuantity<UpdateGoalRequest> validateGoalQuantity) {
        return new Validation<>(
                new JsonSchemaValidationService<>(updateGoalJsonSchemaValidator),
                validateGoalQuantity
        );
    }
}
