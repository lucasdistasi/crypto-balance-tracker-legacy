package com.distasilucas.cryptobalancetracker.mapper.impl.goal;

import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.GoalDuplicatedException;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.GoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_GOAL;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.GOAL_CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.GOAL_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GoalMapper<T extends GoalRequest> implements EntityMapper<Goal, T> {

    private final GoalRepository goalRepository;
    private final CoingeckoService coingeckoService;
    private final UserCryptoRepository userCryptoRepository;

    @Override
    public Goal mapFrom(T input) {
        Goal goal = new Goal();

        if (input instanceof AddGoalRequest addGoalRequest) {
            List<Coin> coins = coingeckoService.retrieveAllCoins();
            String requestCryptoName = addGoalRequest.cryptoName();
            Coin coingeckoCrypto = coins.stream()
                    .filter(crypto -> crypto.getName().equalsIgnoreCase(requestCryptoName))
                    .findFirst()
                    .orElseThrow(() -> new CryptoNotFoundException(String.format(CRYPTO_NAME_NOT_FOUND, requestCryptoName)));
            UserCrypto crypto = userCryptoRepository.findFirstByCryptoId(coingeckoCrypto.getId())
                    .orElseThrow(() -> new CryptoNotFoundException(String.format(GOAL_CRYPTO_NOT_FOUND, coingeckoCrypto.getName())));
            Optional<Goal> existingGoal = goalRepository.findByCryptoId(coingeckoCrypto.getId());

            if (existingGoal.isPresent())
                throw new GoalDuplicatedException(String.format(DUPLICATED_GOAL, addGoalRequest.cryptoName()));

            goal.setCryptoId(crypto.getCryptoId());
            goal.setQuantityGoal(input.quantityGoal());
        }

        if (input instanceof UpdateGoalRequest updateGoalRequest) {
            String goalId = updateGoalRequest.getGoalId();
            Goal existingGoal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new GoalNotFoundException(String.format(GOAL_ID_NOT_FOUND, goalId)));
            Optional<UserCrypto> optionalCrypto = userCryptoRepository.findFirstByCryptoId(existingGoal.getCryptoId());

            if (optionalCrypto.isEmpty())
                throw new CryptoNotFoundException(CRYPTO_NOT_FOUND);

            goal.setGoalId(existingGoal.getGoalId());
            goal.setCryptoId(existingGoal.getCryptoId());
            goal.setQuantityGoal(updateGoalRequest.quantityGoal());
        }

        return goal;
    }
}
