package com.distasilucas.cryptobalancetracker.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class UpdateGoalRequest implements GoalRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String goalId;
    private BigDecimal quantityGoal;

    public UpdateGoalRequest(BigDecimal quantityGoal) {
        this.quantityGoal = quantityGoal;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    @Override
    public BigDecimal quantityGoal() {
        return this.quantityGoal;
    }
}
