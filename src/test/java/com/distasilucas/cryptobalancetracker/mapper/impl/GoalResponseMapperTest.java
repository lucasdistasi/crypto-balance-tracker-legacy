package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.goal.GoalResponseMapper;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalResponseMapperTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    UserCryptoRepository userCryptoRepositoryMock;

    EntityMapper<GoalResponse, Goal> goalResponseMapper;

    @BeforeEach
    void setUp() {
        goalResponseMapper = new GoalResponseMapper(cryptoRepositoryMock, userCryptoRepositoryMock);
    }

    @Test
    void shouldMapGoalSuccessfully() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .quantity(BigDecimal.valueOf(1.15))
                .build();
        var allCryptos = Collections.singletonList(userCrypto);
        var crypto = Crypto.builder()
                .lastKnownPrice(BigDecimal.valueOf(30_000))
                .id("bitcoin")
                .build();
        var goal = new Goal("ABC123", "bitcoin", BigDecimal.valueOf(2));

        when(cryptoRepositoryMock.findById(goal.getCryptoId())).thenReturn(Optional.of(crypto));
        when(userCryptoRepositoryMock.findAllByCryptoId("bitcoin")).thenReturn(Optional.of(allCryptos));

        var goalResponse = goalResponseMapper.mapFrom(goal);

        assertAll(
                () -> assertEquals(goal.getGoalId(), goalResponse.goalId()),
                () -> assertEquals(BigDecimal.valueOf(1.15), goalResponse.actualQuantity()),
                () -> assertEquals(BigDecimal.valueOf(57.50).setScale(2, RoundingMode.HALF_UP), goalResponse.progress()),
                () -> assertEquals(BigDecimal.valueOf(0.85), goalResponse.remainingQuantity()),
                () -> assertEquals(goal.getQuantityGoal(), goalResponse.goalQuantity()),
                () -> assertEquals(BigDecimal.valueOf(25500).setScale(2, RoundingMode.HALF_UP), goalResponse.moneyNeeded())
        );
    }

    @Test
    void shouldMapGoalSuccessfullyWhenGoalReached() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .quantity(BigDecimal.valueOf(1.15))
                .build();
        var allCryptos = Collections.singletonList(userCrypto);
        var crypto = Crypto.builder()
                .lastKnownPrice(BigDecimal.valueOf(30_000))
                .id("bitcoin")
                .build();
        var goal = new Goal("ABC123", "bitcoin", BigDecimal.ONE);

        when(cryptoRepositoryMock.findById(goal.getCryptoId())).thenReturn(Optional.of(crypto));
        when(userCryptoRepositoryMock.findAllByCryptoId("bitcoin")).thenReturn(Optional.of(allCryptos));

        var goalResponse = goalResponseMapper.mapFrom(goal);

        assertAll(
                () -> assertEquals(goal.getGoalId(), goalResponse.goalId()),
                () -> assertEquals(BigDecimal.valueOf(1.15), goalResponse.actualQuantity()),
                () -> assertEquals(BigDecimal.valueOf(100), goalResponse.progress()),
                () -> assertEquals(BigDecimal.ZERO, goalResponse.remainingQuantity()),
                () -> assertEquals(goal.getQuantityGoal(), goalResponse.goalQuantity()),
                () -> assertEquals(BigDecimal.ZERO, goalResponse.moneyNeeded())
        );
    }

    @Test
    void shouldTrowCryptoNotFoundExceptionWhenAddingGoalForNonExistingCoin() {
        var goal = new Goal("ABC123", "bitcoin", BigDecimal.ONE);

        when(cryptoRepositoryMock.findById("bitcoin")).thenReturn(Optional.empty());

        var exception = assertThrows(CryptoNotFoundException.class, () -> goalResponseMapper.mapFrom(goal));

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode()),
                () -> assertEquals(CRYPTO_NOT_FOUND, exception.getMessage())
        );
    }

}