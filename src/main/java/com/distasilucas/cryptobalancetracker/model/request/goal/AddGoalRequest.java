package com.distasilucas.cryptobalancetracker.model.request.goal;

import java.math.BigDecimal;

public record AddGoalRequest(
        String cryptoName,
        BigDecimal quantityGoal
) implements GoalRequest {
}
