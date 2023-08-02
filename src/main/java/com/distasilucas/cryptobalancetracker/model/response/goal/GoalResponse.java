package com.distasilucas.cryptobalancetracker.model.response.goal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoalResponse(
        String id,
        String cryptoName,
        BigDecimal actualQuantity,
        BigDecimal progress,
        BigDecimal remainingQuantity,
        BigDecimal goalQuantity,
        BigDecimal moneyNeeded
) {
}
