package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;

import java.util.List;

public interface GoalService {

    GoalResponse getGoal(String goalId);
    List<GoalResponse> getAllGoals();
    GoalResponse saveGoal(AddGoalRequest addGoalRequest);
    GoalResponse updateGoalQuantity(UpdateGoalRequest updateGoalRequest, String goalId);
    void deleteGoal(String goalId);
}
