package com.distasilucas.cryptobalancetracker.model.request.goal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AddGoalRequest(
        String cryptoName,
        BigDecimal quantityGoal
) implements GoalRequest {
}
