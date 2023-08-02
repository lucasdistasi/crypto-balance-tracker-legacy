package com.distasilucas.cryptobalancetracker.model.request.goal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
