package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.service.GoalService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.GOAL_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.INVALID_ID_MONGO_FORMAT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    GoalRepository goalRepositoryMock;

    @Mock
    UtilValidations utilValidationsMock;

    @Mock
    EntityMapper<Goal, AddGoalRequest> addGoalRequestMapperMock;

    @Mock
    EntityMapper<Goal, UpdateGoalRequest> updateGoalRequestMapperMock;

    @Mock
    EntityMapper<GoalResponse, Goal> goalResponseMapperMock;

    @Mock
    Validation<AddGoalRequest> addGoalRequestValidationMock;

    @Mock
    Validation<UpdateGoalRequest> updateGoalRequestValidationMock;

    GoalService goalService;

    private static final String INVALID_MONGO_ID = "$1nv4l1d";

    @BeforeEach
    void setUp() {
        goalService = new GoalServiceImpl(goalRepositoryMock, utilValidationsMock, addGoalRequestMapperMock,
                updateGoalRequestMapperMock, goalResponseMapperMock, addGoalRequestValidationMock, updateGoalRequestValidationMock);
    }

    @Test
    void shouldReturnGoal() {
        var goal = MockData.getGoal();
        var mockGoalResponse = MockData.getGoalResponse();

        doNothing().when(utilValidationsMock).validateIdMongoEntityFormat("ABC123");
        when(goalRepositoryMock.findById("ABC123")).thenReturn(Optional.of(goal));
        when(goalResponseMapperMock.mapFrom(goal)).thenReturn(mockGoalResponse);

        var goalResponse = goalService.getGoal("ABC123");

        assertAll(
                () -> assertEquals(mockGoalResponse.goalId(), goalResponse.goalId()),
                () -> assertEquals(mockGoalResponse.cryptoName(), goalResponse.cryptoName()),
                () -> assertEquals(mockGoalResponse.actualQuantity(), goalResponse.actualQuantity()),
                () -> assertEquals(mockGoalResponse.progress(), goalResponse.progress()),
                () -> assertEquals(mockGoalResponse.goalQuantity(), goalResponse.goalQuantity()),
                () -> assertEquals(mockGoalResponse.moneyNeeded(), goalResponse.moneyNeeded())
        );
    }

    @Test
    void shouldThrowExceptionWhenRetrievingGoalWithInvalidGoalID() {
        var apiValidationException = new ApiValidationException(INVALID_ID_MONGO_FORMAT);

        doThrow(apiValidationException).when(utilValidationsMock).validateIdMongoEntityFormat(INVALID_MONGO_ID);

        var exception = assertThrows(ApiValidationException.class, () -> goalService.getGoal(INVALID_MONGO_ID));

        assertEquals(INVALID_ID_MONGO_FORMAT, exception.getErrorMessage());
    }

    @Test
    void shouldThrowGoalNotFoundException() {
        var expectedMessage = String.format(GOAL_ID_NOT_FOUND, "ABC123");

        when(goalRepositoryMock.findById("ABC123")).thenReturn(Optional.empty());

        var exception = assertThrows(GoalNotFoundException.class, () -> goalService.getGoal("ABC123"));

        assertEquals(expectedMessage, exception.getErrorMessage());
    }

    @Test
    void shouldReturnAllGoals() {
        var goal = MockData.getGoal();
        var goals = Collections.singletonList(goal);
        var goalResponse = MockData.getGoalResponse();

        when(goalRepositoryMock.findAll()).thenReturn(goals);
        when(goalResponseMapperMock.mapFrom(goal)).thenReturn(goalResponse);

        var allGoals = goalService.getAllGoals();

        verify(goalResponseMapperMock, times(1)).mapFrom(any());
        assertAll(
                () -> assertEquals(1, allGoals.size()),
                () -> assertEquals(goalResponse.goalId(), allGoals.get(0).goalId()),
                () -> assertEquals(goalResponse.cryptoName(), allGoals.get(0).cryptoName()),
                () -> assertEquals(goalResponse.actualQuantity(), allGoals.get(0).actualQuantity()),
                () -> assertEquals(goalResponse.progress(), allGoals.get(0).progress()),
                () -> assertEquals(goalResponse.goalQuantity(), allGoals.get(0).goalQuantity()),
                () -> assertEquals(goalResponse.moneyNeeded(), allGoals.get(0).moneyNeeded())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoGoalsFound() {
        when(goalRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var allGoals = goalService.getAllGoals();

        verify(goalResponseMapperMock, never()).mapFrom(any());
        assertAll(
                () -> assertEquals(0, allGoals.size())
        );
    }

    @Test
    void shouldSaveGoal() {
        var goal = MockData.getGoal();
        var addGoalRequest = new AddGoalRequest("bitcoin", BigDecimal.ONE);
        var mockGoalResponse = MockData.getGoalResponse();

        when(addGoalRequestMapperMock.mapFrom(addGoalRequest)).thenReturn(goal);
        when(goalRepositoryMock.save(goal)).thenReturn(goal);
        when(goalResponseMapperMock.mapFrom(goal)).thenReturn(mockGoalResponse);

        var goalResponse = goalService.saveGoal(addGoalRequest);

        verify(goalRepositoryMock, times(1)).save(goal);

        assertAll(
                () -> assertEquals(addGoalRequest.cryptoName(), goalResponse.cryptoName()),
                () -> assertEquals(addGoalRequest.quantityGoal(), goalResponse.goalQuantity())
        );
    }

    @Test
    void shouldUpdateGoalQuantity() {
        var updateGoalRequest = new UpdateGoalRequest(BigDecimal.ONE);
        var goal = MockData.getGoal();
        goal.setQuantityGoal(BigDecimal.TEN);

        var mockGoalResponse = MockData.getGoalResponse();

        when(updateGoalRequestMapperMock.mapFrom(updateGoalRequest)).thenReturn(goal);
        when(goalRepositoryMock.save(goal)).thenReturn(goal);
        when(goalResponseMapperMock.mapFrom(goal)).thenReturn(mockGoalResponse);

        var goalResponse = goalService.updateGoalQuantity(updateGoalRequest, "ABC123");

        assertAll(
                () -> assertEquals(mockGoalResponse.goalId(), goalResponse.goalId()),
                () -> assertEquals(mockGoalResponse.cryptoName(), goalResponse.cryptoName()),
                () -> assertEquals(mockGoalResponse.actualQuantity(), goalResponse.actualQuantity()),
                () -> assertEquals(mockGoalResponse.progress(), goalResponse.progress()),
                () -> assertEquals(mockGoalResponse.goalQuantity(), goalResponse.goalQuantity()),
                () -> assertEquals(mockGoalResponse.moneyNeeded(), goalResponse.moneyNeeded())
        );
    }

    @Test
    void shouldDeleteGoal() {
        var goal = MockData.getGoal();
        var mongoEntityId = "ABC123";

        doNothing().when(utilValidationsMock).validateIdMongoEntityFormat(mongoEntityId);
        when(goalRepositoryMock.findById(mongoEntityId)).thenReturn(Optional.of(goal));

        goalService.deleteGoal(mongoEntityId);

        verify(goalRepositoryMock, times(1)).deleteById(mongoEntityId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingGoalWithInvalidID() {
        var mongoEntityId = "$1NV4L1D";
        var apiValidationException = new ApiValidationException(INVALID_ID_MONGO_FORMAT);

        doThrow(apiValidationException).when(utilValidationsMock).validateIdMongoEntityFormat(mongoEntityId);

        var exception = assertThrows(ApiValidationException.class, () -> goalService.deleteGoal(mongoEntityId));

        assertEquals(INVALID_ID_MONGO_FORMAT, exception.getErrorMessage());
    }

    @Test
    void shouldThrowGoalNotFoundExceptionWhenDeletingNonExistingGoal() {
        var mongoEntityId = "ABC123";
        var expectedMessage = String.format(GOAL_ID_NOT_FOUND, "ABC123");

        doNothing().when(utilValidationsMock).validateIdMongoEntityFormat(mongoEntityId);
        when(goalRepositoryMock.findById(mongoEntityId)).thenReturn(Optional.empty());

        var exception = assertThrows(GoalNotFoundException.class, () -> goalService.deleteGoal(mongoEntityId));

        assertEquals(expectedMessage, exception.getErrorMessage());
    }
}