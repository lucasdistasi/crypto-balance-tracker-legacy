package com.distasilucas.cryptobalancetracker.model.request;

import java.math.BigDecimal;

public record AddGoalRequest(
        String cryptoName,
        BigDecimal quantityGoal
) implements GoalRequest {
}
