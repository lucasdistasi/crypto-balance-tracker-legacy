package com.distasilucas.cryptobalancetracker.validation.goal;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoalCryptoNameValidatorTest {

    EntityValidation<AddGoalRequest> entityValidation = new GoalCryptoNameValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "bitcoin", "oasis network", "1inch", "0x Protocol", "baby doge coin", "b"
    })
    void shouldValidateSuccessfully(String cryptoName) {
        var addGoalRequest = new AddGoalRequest(cryptoName, BigDecimal.ONE);
        entityValidation.validate(addGoalRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " bitcoin", "bitcoin ", "bit  coin", "$bitcoin", "bitcoin$", "bit.coin", "bit_coin", "bit-coin", "bit/coin"
    })
    void shouldThrowExceptionForInvalidCryptoName(String cryptoName) {
        var addGoalRequest = new AddGoalRequest(cryptoName, BigDecimal.ONE);

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(addGoalRequest));

        assertEquals("Invalid crypto name", apiValidationException.getMessage());
    }
}