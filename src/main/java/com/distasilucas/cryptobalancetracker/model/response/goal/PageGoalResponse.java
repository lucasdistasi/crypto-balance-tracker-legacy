package com.distasilucas.cryptobalancetracker.model.response.goal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PageGoalResponse(
    int page,
    int totalPages,
    boolean hasNextPage,
    List<GoalResponse> goals
) {

    public PageGoalResponse(int page, int totalPages, List<GoalResponse> goals) {
        this(page + 1, totalPages, page < totalPages - 1, goals);
    }
}