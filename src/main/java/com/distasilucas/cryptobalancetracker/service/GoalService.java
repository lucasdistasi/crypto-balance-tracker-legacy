package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;

import java.util.List;
import java.util.Optional;

public interface GoalService {

    Optional<Goal> findById(String id);
    GoalResponse getGoalResponse(String goalId);
    List<GoalResponse> getAllGoalsResponse();
    GoalResponse saveGoal(AddGoalRequest addGoalRequest);
    GoalResponse updateGoalQuantity(UpdateGoalRequest updateGoalRequest, String goalId);
    void deleteGoal(String goalId);
    Optional<Goal> findByCryptoId(String cryptoId);
}
