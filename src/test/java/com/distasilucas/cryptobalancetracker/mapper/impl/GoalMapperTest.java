package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.GoalDuplicatedException;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.goal.GoalMapper;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_GOAL;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.GOAL_CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.GOAL_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalMapperTest {

    @Mock
    GoalRepository goalRepositoryMock;

    @Mock
    CoingeckoService coingeckoServiceMock;

    @Mock
    UserCryptoRepository userCryptoRepositoryMock;

    EntityMapper<Goal, AddGoalRequest> addGoalRequestMapper;
    EntityMapper<Goal, UpdateGoalRequest> updateGoalRequestMapper;

    @BeforeEach
    void setUp() {
        addGoalRequestMapper = new GoalMapper<>(goalRepositoryMock, coingeckoServiceMock, userCryptoRepositoryMock);
        updateGoalRequestMapper = new GoalMapper<>(goalRepositoryMock, coingeckoServiceMock, userCryptoRepositoryMock);
    }

    @Test
    void shouldMapAddGoalRequestSuccessfully() {
        var allCoins = MockData.getAllCoins();
        var crypto = UserCrypto.builder()
                .cryptoId("ethereum")
                .build();
        var addGoalRequest = new AddGoalRequest("ethereum", BigDecimal.TEN);

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(userCryptoRepositoryMock.findFirstByCryptoId("ethereum")).thenReturn(Optional.of(crypto));

        var goal = addGoalRequestMapper.mapFrom(addGoalRequest);

        assertAll(
                () -> assertEquals(addGoalRequest.quantityGoal(), goal.getQuantityGoal()),
                () -> assertEquals(allCoins.get(0).getId(), goal.getCryptoId())
        );
    }

    @Test
    void shouldThrowCryptoNotFoundExceptionWhenMappingAddGoalRequest() {
        var allCoins = MockData.getAllCoins();
        var addGoalRequest = new AddGoalRequest("pepe", BigDecimal.TEN);
        var expectedMessage = String.format(CRYPTO_NAME_NOT_FOUND, addGoalRequest.cryptoName());

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);

        var exception = assertThrows(CryptoNotFoundException.class, () -> addGoalRequestMapper.mapFrom(addGoalRequest));

        assertEquals(expectedMessage, exception.getErrorMessage());
    }

    @Test
    void shouldThrowCryptoNotFoundExceptionWhenAddingGoalForNotOwnedCrypto() {
        var allCoins = MockData.getAllCoins();
        var addGoalRequest = new AddGoalRequest("ethereum", BigDecimal.TEN);
        var coin = allCoins.get(0);
        var expectedMessage = String.format(GOAL_CRYPTO_NOT_FOUND, coin.getName());

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(userCryptoRepositoryMock.findFirstByCryptoId(coin.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(CryptoNotFoundException.class, () -> addGoalRequestMapper.mapFrom(addGoalRequest));

        assertEquals(expectedMessage, exception.getErrorMessage());
    }

    @Test
    void shouldThrowGoalDuplicatedExceptionWhenAddingDuplicatedGoal() {
        var allCoins = MockData.getAllCoins();
        var addGoalRequest = new AddGoalRequest("ethereum", BigDecimal.TEN);
        var coin = allCoins.get(0);
        var crypto = UserCrypto.builder()
                .cryptoId("ethereum")
                .build();
        var goal = Goal.builder()
                .id("ABC123")
                .build();
        var expectedMessage = String.format(DUPLICATED_GOAL, addGoalRequest.cryptoName());

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(userCryptoRepositoryMock.findFirstByCryptoId(coin.getId())).thenReturn(Optional.of(crypto));
        when(goalRepositoryMock.findByCryptoId(coin.getId())).thenReturn(Optional.of(goal));

        var exception = assertThrows(GoalDuplicatedException.class, () -> addGoalRequestMapper.mapFrom(addGoalRequest));

        assertEquals(expectedMessage, exception.getErrorMessage());
    }

    @Test
    void shouldMapUpdateGoalRequestSuccessfully() {
        var updateGoalRequest = new UpdateGoalRequest(BigDecimal.valueOf(1.15));
        updateGoalRequest.setGoalId("ABC123");
        var existingGoal = Goal.builder()
                .id("ABC123")
                .cryptoId("ethereum")
                .build();
        var crypto = UserCrypto.builder()
                .cryptoId("ethereum")
                .build();

        when(goalRepositoryMock.findById(updateGoalRequest.getGoalId())).thenReturn(Optional.of(existingGoal));
        when(userCryptoRepositoryMock.findFirstByCryptoId(existingGoal.getCryptoId())).thenReturn(Optional.of(crypto));

        var goal = updateGoalRequestMapper.mapFrom(updateGoalRequest);

        assertAll(
                () -> assertEquals(existingGoal.getId(), goal.getId()),
                () -> assertEquals(existingGoal.getCryptoId(), goal.getCryptoId()),
                () -> assertEquals(updateGoalRequest.getQuantityGoal(), goal.getQuantityGoal())
        );
    }

    @Test
    void shouldThrowGoalNotFoundExceptionWhenUpdatingNonExistentGoal() {
        var updateGoalRequest = new UpdateGoalRequest(BigDecimal.valueOf(1.15));
        updateGoalRequest.setGoalId("ABC123");
        var expectedMessage = String.format(GOAL_ID_NOT_FOUND, updateGoalRequest.getGoalId());

        when(goalRepositoryMock.findById(updateGoalRequest.getGoalId())).thenReturn(Optional.empty());

        var exception = assertThrows(GoalNotFoundException.class, () -> updateGoalRequestMapper.mapFrom(updateGoalRequest));

        assertEquals(expectedMessage, exception.getErrorMessage());
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenUpdatingGoal() {
        var updateGoalRequest = new UpdateGoalRequest(BigDecimal.valueOf(1.15));
        updateGoalRequest.setGoalId("ABC123");
        var goal = Goal.builder()
                .id("ABC123")
                .cryptoId("Ethereum")
                .build();

        when(goalRepositoryMock.findById(updateGoalRequest.getGoalId())).thenReturn(Optional.of(goal));
        when(userCryptoRepositoryMock.findFirstByCryptoId(goal.getCryptoId())).thenReturn(Optional.empty());

        var exception = assertThrows(CryptoNotFoundException.class, () -> updateGoalRequestMapper.mapFrom(updateGoalRequest));

        assertEquals(CRYPTO_NOT_FOUND, exception.getErrorMessage());
    }

}