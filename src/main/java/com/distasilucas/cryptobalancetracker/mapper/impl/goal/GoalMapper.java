package com.distasilucas.cryptobalancetracker.mapper.impl.goal;

import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.GoalDuplicatedException;
import com.distasilucas.cryptobalancetracker.exception.GoalNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.goal.AddGoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.GoalRequest;
import com.distasilucas.cryptobalancetracker.model.request.goal.UpdateGoalRequest;
import com.distasilucas.cryptobalancetracker.service.GoalService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_GOAL;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.GOAL_ID_NOT_FOUND;

@Service
public class GoalMapper<T extends GoalRequest> implements EntityMapper<Goal, T> {

    private final GoalService goalService;
    private final CoingeckoService coingeckoService;

    public GoalMapper(@Lazy GoalService goalService,
                      CoingeckoService coingeckoService) {
        this.goalService = goalService;
        this.coingeckoService = coingeckoService;
    }

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
            Optional<Goal> existingGoal = goalService.findByCryptoId(coingeckoCrypto.getId());

            if (existingGoal.isPresent())
                throw new GoalDuplicatedException(String.format(DUPLICATED_GOAL, addGoalRequest.cryptoName()));

            goal.setCryptoId(coingeckoCrypto.getId());
            goal.setQuantityGoal(input.quantityGoal());
        }

        if (input instanceof UpdateGoalRequest updateGoalRequest) {
            String goalId = updateGoalRequest.getGoalId();
            Goal existingGoal = goalService.findById(goalId)
                    .orElseThrow(() -> new GoalNotFoundException(String.format(GOAL_ID_NOT_FOUND, goalId)));

            goal.setId(existingGoal.getId());
            goal.setCryptoId(existingGoal.getCryptoId());
            goal.setQuantityGoal(updateGoalRequest.quantityGoal());
        }

        return goal;
    }
}
