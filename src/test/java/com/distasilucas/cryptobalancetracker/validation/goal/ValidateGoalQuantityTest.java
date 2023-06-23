package com.distasilucas.cryptobalancetracker.validation.goal;

import com.distasilucas.cryptobalancetracker.model.request.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.GoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import com.distasilucas.cryptobalancetracker.validation.QuantityValueValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ValidateGoalQuantityTest {

    @Mock
    QuantityValueValidator quantityValueValidatorMock;

    EntityValidation<GoalRequest> validateGoalQuantity;

    @BeforeEach
    void setUp() {
        validateGoalQuantity = new ValidateGoalQuantity<>(quantityValueValidatorMock);
    }

    @Test
    void shouldValidateQuantitySuccessfullyWhenAddingGoal() {
        var goalRequest = new AddGoalRequest("bitcoin", BigDecimal.ONE);

        validateGoalQuantity.validate(goalRequest);

        verify(quantityValueValidatorMock, times(1)).validateCryptoQuantity(goalRequest.quantityGoal());
    }

    @Test
    void shouldValidateQuantitySuccessfullyWhenUpdatingGoalQuantity() {
        var updateGoalRequest = new UpdateGoalRequest(BigDecimal.ONE);

        validateGoalQuantity.validate(updateGoalRequest);

        verify(quantityValueValidatorMock, times(1)).validateCryptoQuantity(updateGoalRequest.quantityGoal());
    }
}