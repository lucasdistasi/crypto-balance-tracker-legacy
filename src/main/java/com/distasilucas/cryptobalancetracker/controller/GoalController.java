package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.controller.swagger.GoalControllerApi;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
import com.distasilucas.cryptobalancetracker.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
@CrossOrigin(origins = {"*"})
@PreAuthorize("@securityService.isSecurityDisabled() OR hasAuthority('ROLE_ADMIN')")
public class GoalController implements GoalControllerApi {

    private final GoalService goalService;

    @Override
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable String goalId) {
        GoalResponse goalResponse = goalService.getGoal(goalId);

        return ResponseEntity.ok(goalResponse);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals() {
        List<GoalResponse> goalsResponse = goalService.getAllGoals();
        HttpStatus httpStatus = CollectionUtils.isNotEmpty(goalsResponse) ? HttpStatus.OK : HttpStatus.NO_CONTENT;

        return ResponseEntity.status(httpStatus)
                .body(goalsResponse);
    }

    @Override
    @PostMapping
    public ResponseEntity<GoalResponse> addGoal(@RequestBody AddGoalRequest addGoalRequest) {
        GoalResponse goalResponse = goalService.saveGoal(addGoalRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goalResponse);
    }

    @Override
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponse> updateGoalQuantity(@RequestBody UpdateGoalRequest updateGoalRequest,
                                                           @PathVariable String goalId) {
        GoalResponse goalResponse = goalService.updateGoalQuantity(updateGoalRequest, goalId);

        return ResponseEntity.ok(goalResponse);
    }

    @Override
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String goalId) {
        goalService.deleteGoal(goalId);

        return ResponseEntity.noContent().build();
    }
}
