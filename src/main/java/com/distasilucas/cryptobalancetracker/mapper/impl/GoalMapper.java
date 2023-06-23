package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.GoalDuplicatedException;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.GoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.*;

@Service
@RequiredArgsConstructor
public class GoalMapper<T extends GoalRequest> implements EntityMapper<Goal, T> {

    private final GoalRepository goalRepository;
    private final CoingeckoService coingeckoService;
    private final CryptoRepository cryptoRepository;

    @Override
    public Goal mapFrom(T input) {
        List<Coin> coins = coingeckoService.retrieveAllCoins();
        Goal goal = new Goal();

        if (input instanceof AddGoalRequest addGoalRequest) {
            String requestCryptoName = addGoalRequest.cryptoName();
            Coin coingeckoCrypto = coins.stream()
                    .filter(crypto -> crypto.getName().equalsIgnoreCase(requestCryptoName))
                    .findFirst()
                    .orElseThrow(() -> new CoinNotFoundException(String.format(COIN_NAME_NOT_FOUND, requestCryptoName)));
            String cryptoName = coingeckoCrypto.getName();
            Crypto crypto = cryptoRepository.findFirstByName(cryptoName)
                    .orElseThrow(() -> new CoinNotFoundException(String.format(GOAL_CRYPTO_NOT_FOUND, cryptoName)));
            Optional<Goal> existingGoal = goalRepository.findByCryptoId(coingeckoCrypto.getId());

            if (existingGoal.isPresent())
                throw new GoalDuplicatedException(String.format(DUPLICATED_GOAL, addGoalRequest.cryptoName()));

            goal.setCryptoId(crypto.getCoinId());
            goal.setCryptoName(crypto.getName());
            goal.setQuantityGoal(input.quantityGoal());
        }

        if (input instanceof UpdateGoalRequest updateGoalRequest) {
            String goalId = updateGoalRequest.getGoalId();
            Goal existingGoal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new GoalNotFoundException(String.format(GOAL_ID_NOT_FOUND, goalId)));
            Optional<Crypto> optionalCrypto = cryptoRepository.findFirstByName(existingGoal.getCryptoName());

            if (optionalCrypto.isEmpty())
                throw new CoinNotFoundException(String.format(COIN_NAME_NOT_FOUND, existingGoal.getCryptoName()));

            goal.setGoalId(existingGoal.getGoalId());
            goal.setCryptoId(existingGoal.getCryptoId());
            goal.setCryptoName(existingGoal.getCryptoName());
            goal.setQuantityGoal(updateGoalRequest.quantityGoal());
        }

        return goal;
    }
}
