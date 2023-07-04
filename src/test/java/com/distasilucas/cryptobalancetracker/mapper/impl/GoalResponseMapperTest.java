package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.goal.GoalResponseMapper;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_NAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalResponseMapperTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    EntityMapper<GoalResponse, Goal> goalResponseMapper;

    @BeforeEach
    void setUp() {
        goalResponseMapper = new GoalResponseMapper(cryptoRepositoryMock);
    }

    @Test
    void shouldMapGoalSuccessfully() {
        var allCryptos = MockData.getAllCryptos();
        var crypto = Crypto.builder()
                .lastKnownPrice(BigDecimal.valueOf(30_000))
                .build();
        var goal = new Goal("ABC123", "bitcoin", "bitcoin", BigDecimal.valueOf(2));

        when(cryptoRepositoryMock.findFirstByName(goal.getCryptoName())).thenReturn(Optional.of(crypto));
        when(cryptoRepositoryMock.findAllByCoinId("bitcoin")).thenReturn(Optional.of(allCryptos));

        var goalResponse = goalResponseMapper.mapFrom(goal);

        assertAll(
                () -> assertEquals(goal.getGoalId(), goalResponse.goalId()),
                () -> assertEquals(goal.getCryptoName(), goalResponse.cryptoName()),
                () -> assertEquals(BigDecimal.valueOf(1.15), goalResponse.actualQuantity()),
                () -> assertEquals(BigDecimal.valueOf(57.50).setScale(2, RoundingMode.HALF_UP), goalResponse.progress()),
                () -> assertEquals(BigDecimal.valueOf(0.85), goalResponse.remainingQuantity()),
                () -> assertEquals(goal.getQuantityGoal(), goalResponse.goalQuantity()),
                () -> assertEquals(BigDecimal.valueOf(25500).setScale(2, RoundingMode.HALF_UP), goalResponse.moneyNeeded())
        );
    }

    @Test
    void shouldMapGoalSuccessfullyWhenGoalReached() {
        var allCryptos = MockData.getAllCryptos();
        var crypto = Crypto.builder()
                .lastKnownPrice(BigDecimal.valueOf(30_000))
                .build();
        var goal = new Goal("ABC123", "bitcoin", "bitcoin", BigDecimal.ONE);

        when(cryptoRepositoryMock.findFirstByName(goal.getCryptoName())).thenReturn(Optional.of(crypto));
        when(cryptoRepositoryMock.findAllByCoinId("bitcoin")).thenReturn(Optional.of(allCryptos));

        var goalResponse = goalResponseMapper.mapFrom(goal);

        assertAll(
                () -> assertEquals(goal.getGoalId(), goalResponse.goalId()),
                () -> assertEquals(goal.getCryptoName(), goalResponse.cryptoName()),
                () -> assertEquals(BigDecimal.valueOf(1.15), goalResponse.actualQuantity()),
                () -> assertEquals(BigDecimal.valueOf(100), goalResponse.progress()),
                () -> assertEquals(BigDecimal.ZERO, goalResponse.remainingQuantity()),
                () -> assertEquals(goal.getQuantityGoal(), goalResponse.goalQuantity()),
                () -> assertEquals(BigDecimal.ZERO, goalResponse.moneyNeeded())
        );
    }

    @Test
    void shouldTrowCoinNotFoundExceptionWhenAddingGoalForNonExistingCoin() {
        var goal = new Goal("ABC123", "bitcoin", "bitcoin", BigDecimal.ONE);
        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, goal.getCryptoName());

        when(cryptoRepositoryMock.findFirstByName("bitcoin")).thenReturn(Optional.empty());

        var exception = assertThrows(CoinNotFoundException.class, () -> goalResponseMapper.mapFrom(goal));

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode()),
                () -> assertEquals(expectedMessage, exception.getMessage())
        );
    }

}