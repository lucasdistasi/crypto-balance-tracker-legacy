package com.distasilucas.cryptobalancetracker.validation.goal;

import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.springframework.stereotype.Service;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_CRYPTO_NAME;
import static com.distasilucas.cryptobalancetracker.constant.RegexConstants.CRYPTO_NAME_REGEX_VALIDATION;

@Service
public class GoalCryptoNameValidator implements EntityValidation<AddGoalRequest> {

    @Override
    public void validate(AddGoalRequest addGoalRequest) {
        String cryptoName = addGoalRequest.cryptoName();

        if (!cryptoName.matches(CRYPTO_NAME_REGEX_VALIDATION)) {
            throw new ApiValidationException(INVALID_CRYPTO_NAME);
        }
    }
}
