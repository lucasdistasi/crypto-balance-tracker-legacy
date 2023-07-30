package com.distasilucas.cryptobalancetracker.model.response.goal;

import java.util.List;

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