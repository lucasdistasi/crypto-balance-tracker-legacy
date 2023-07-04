package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    GoalService goalServiceMock;

    GoalController goalController;

    @BeforeEach
    void setUp() {
        goalController = new GoalController(goalServiceMock);
    }

    @Test
    void shouldRetrieveGoalWith200StatusCode() {
        var goalResponse = MockData.getGoalResponse();

        when(goalServiceMock.getGoal("ABC123")).thenReturn(goalResponse);

        var responseEntity = goalController.getGoal("ABC123");

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(goalResponse.goalId(), responseEntity.getBody().goalId()),
                () -> assertEquals(goalResponse.cryptoName(), responseEntity.getBody().cryptoName()),
                () -> assertEquals(goalResponse.actualQuantity(), responseEntity.getBody().actualQuantity()),
                () -> assertEquals(goalResponse.progress(), responseEntity.getBody().progress()),
                () -> assertEquals(goalResponse.goalQuantity(), responseEntity.getBody().goalQuantity()),
                () -> assertEquals(goalResponse.moneyNeeded(), responseEntity.getBody().moneyNeeded())
        );
    }

    @Test
    void shouldRetrieveAllGoals() {
        var goalResponse = Collections.singletonList(MockData.getGoalResponse());

        when(goalServiceMock.getAllGoals()).thenReturn(goalResponse);

        var responseEntity = goalController.getAllGoals();

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().size())
        );
    }

    @Test
    void shouldReturnNoContentWhenNoGoalsFound() {
        when(goalServiceMock.getAllGoals()).thenReturn(Collections.emptyList());

        var responseEntity = goalController.getAllGoals();

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode()),
                () -> assertEquals(0, responseEntity.getBody().size())
        );
    }

    @Test
    void shouldAddGoal() {
        var goalResponse = MockData.getGoalResponse();
        var addGoalRequest = new AddGoalRequest("bitcoin", BigDecimal.ONE);

        when(goalServiceMock.saveGoal(addGoalRequest)).thenReturn(goalResponse);

        var responseEntity = goalController.addGoal(addGoalRequest);

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode()),
                () -> assertEquals(goalResponse.goalId(), responseEntity.getBody().goalId()),
                () -> assertEquals(goalResponse.cryptoName(), responseEntity.getBody().cryptoName()),
                () -> assertEquals(goalResponse.actualQuantity(), responseEntity.getBody().actualQuantity()),
                () -> assertEquals(goalResponse.progress(), responseEntity.getBody().progress()),
                () -> assertEquals(goalResponse.goalQuantity(), responseEntity.getBody().goalQuantity()),
                () -> assertEquals(goalResponse.moneyNeeded(), responseEntity.getBody().moneyNeeded())
        );
    }

    @Test
    void shouldUpdateGoalQuantity() {
        var goalResponse = MockData.getGoalResponse();
        var updateGoalQuantityRequest = new UpdateGoalRequest(BigDecimal.valueOf(0.5));

        when(goalServiceMock.updateGoalQuantity(updateGoalQuantityRequest, "ABC123")).thenReturn(goalResponse);

        var responseEntity = goalController.updateGoalQuantity(updateGoalQuantityRequest, "ABC123");

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(goalResponse.goalId(), responseEntity.getBody().goalId()),
                () -> assertEquals(goalResponse.cryptoName(), responseEntity.getBody().cryptoName()),
                () -> assertEquals(goalResponse.actualQuantity(), responseEntity.getBody().actualQuantity()),
                () -> assertEquals(goalResponse.progress(), responseEntity.getBody().progress()),
                () -> assertEquals(goalResponse.goalQuantity(), responseEntity.getBody().goalQuantity()),
                () -> assertEquals(goalResponse.moneyNeeded(), responseEntity.getBody().moneyNeeded())
        );
    }

    @Test
    void shouldDeleteGoal() {
        doNothing().when(goalServiceMock).deleteGoal("ABC123");

        var responseEntity = goalController.deleteGoal("ABC123");

        assertNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

}