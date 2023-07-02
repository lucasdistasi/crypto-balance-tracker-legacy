package com.distasilucas.cryptobalancetracker.model.response.goal;

import java.math.BigDecimal;

public record GoalResponse(
        String goalId,
        String cryptoName,
        BigDecimal actualQuantity,
        BigDecimal progress,
        BigDecimal remainingQuantity,
        BigDecimal goalQuantity,
        BigDecimal moneyNeeded
) {
}
