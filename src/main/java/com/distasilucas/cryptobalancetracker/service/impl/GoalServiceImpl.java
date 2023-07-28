package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.service.GoalService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.GOAL_ID_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UtilValidations utilValidations;
    private final EntityMapper<Goal, AddGoalRequest> addGoalRequestMapper;
    private final EntityMapper<Goal, UpdateGoalRequest> updateGoalRequestMapper;
    private final EntityMapper<GoalResponse, Goal> goalResponseMapper;
    private final Validation<AddGoalRequest> addGoalRequestValidation;
    private final Validation<UpdateGoalRequest> updateGoalRequestValidation;

    @Override
    public GoalResponse getGoal(String goalId) {
        utilValidations.validateIdMongoEntityFormat(goalId);
        log.info("Trying to retrieve info for goalId {}", goalId);
        Goal goal = getGoalById(goalId);

        return goalResponseMapper.mapFrom(goal);
    }

    @Override
    public List<GoalResponse> getAllGoals() {
        log.info("Retrieving all goals");

        return goalRepository.findAll()
                .stream()
                .map(goalResponseMapper::mapFrom)
                .sorted(Comparator.comparing(GoalResponse::moneyNeeded))
                .toList();
    }

    @Override
    public GoalResponse saveGoal(AddGoalRequest addGoalRequest) {
        addGoalRequestValidation.validate(addGoalRequest);

        Goal goal = addGoalRequestMapper.mapFrom(addGoalRequest);
        Goal savedGoal = goalRepository.save(goal);
        log.info("Saved goal {}", savedGoal);

        return goalResponseMapper.mapFrom(savedGoal);
    }

    @Override
    public GoalResponse updateGoalQuantity(UpdateGoalRequest updateGoalRequest, String goalId) {
        updateGoalRequest.setGoalId(goalId);
        updateGoalRequestValidation.validate(updateGoalRequest);
        Goal goal = updateGoalRequestMapper.mapFrom(updateGoalRequest);

        log.info("Updating goal quantity of goal id {}. New goal quantity: {}", goal.getId(), updateGoalRequest.quantityGoal());
        Goal newGoal = goalRepository.save(goal);

        return goalResponseMapper.mapFrom(newGoal);
    }

    @Override
    public void deleteGoal(String goalId) {
        utilValidations.validateIdMongoEntityFormat(goalId);
        Goal goal = getGoalById(goalId);
        log.info("Deleting goal {}", goal);

        goalRepository.deleteById(goalId);
    }

    private Goal getGoalById(String goalId) {
        Optional<Goal> goal = goalRepository.findById(goalId);

        if (goal.isEmpty()) {
            String message = String.format(GOAL_ID_NOT_FOUND, goalId);

            throw new GoalNotFoundException(message);
        }

        return goal.get();
    }
}
