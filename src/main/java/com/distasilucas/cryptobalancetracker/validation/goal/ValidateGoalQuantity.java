package com.distasilucas.cryptobalancetracker.validation.goal;

import com.distasilucas.cryptobalancetracker.model.request.goal.GoalRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import com.distasilucas.cryptobalancetracker.validation.QuantityValueValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateGoalQuantity<T extends GoalRequest> implements EntityValidation<T> {

    private final QuantityValueValidator quantityValueValidator;

    @Override
    public void validate(GoalRequest goalRequest) {
        quantityValueValidator.validateCryptoQuantity(goalRequest.quantityGoal());
    }
}
