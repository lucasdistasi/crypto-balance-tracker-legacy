package com.distasilucas.cryptobalancetracker.mapper.impl.goal;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.goal.GoalResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalResponseMapper implements EntityMapper<GoalResponse, Goal> {

    private final CryptoRepository cryptoRepository;
    private final UserCryptoRepository userCryptoRepository;

    @Override
    public GoalResponse mapFrom(Goal input) {
        String cryptoId = input.getCryptoId();
        log.info("Mapping {}", cryptoId);
        Crypto crypto = cryptoRepository.findById(cryptoId)
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));

        BigDecimal priceInUsd = crypto.getLastKnownPrice();
        BigDecimal actualQuantity = getActualQuantity(crypto.getId());
        BigDecimal quantityGoal = input.getQuantityGoal();
        BigDecimal moneyNeeded = getMoneyNeeded(priceInUsd, actualQuantity, quantityGoal);
        BigDecimal progress = getProgress(actualQuantity, quantityGoal);
        BigDecimal remainingQuantity = getRemainingQuantity(actualQuantity, quantityGoal);

        return new GoalResponse(input.getGoalId(), crypto.getName(), actualQuantity, progress,
                remainingQuantity, quantityGoal, moneyNeeded);
    }

    private BigDecimal getActualQuantity(String cryptoId) {
        Optional<List<UserCrypto>> optionalCryptos = userCryptoRepository.findAllByCryptoId(cryptoId);
        BigDecimal totalQuantity = BigDecimal.ZERO;

        if (optionalCryptos.isPresent()) {
            List<BigDecimal> cryptosQuantity = optionalCryptos.get()
                    .stream()
                    .map(UserCrypto::getQuantity)
                    .toList();

            for (BigDecimal quantity : cryptosQuantity) {
                totalQuantity = totalQuantity.add(quantity);
            }
        }

        return totalQuantity;
    }

    private BigDecimal getProgress(BigDecimal actualQuantity, BigDecimal quantityGoal) {
        return isActualQuantityEqualOrGreaterThanGoal(actualQuantity, quantityGoal) ?
                BigDecimal.valueOf(100) :
                actualQuantity
                        .multiply(BigDecimal.valueOf(100))
                        .divide(quantityGoal, RoundingMode.HALF_UP)
                        .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getMoneyNeeded(BigDecimal priceInUsd, BigDecimal actualQuantity, BigDecimal quantityGoal) {
        return isActualQuantityEqualOrGreaterThanGoal(actualQuantity, quantityGoal) ?
                BigDecimal.ZERO :
                priceInUsd.multiply(quantityGoal.subtract(actualQuantity))
                        .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getRemainingQuantity(BigDecimal actualQuantity, BigDecimal quantityGoal) {
        return isActualQuantityEqualOrGreaterThanGoal(actualQuantity, quantityGoal) ?
                BigDecimal.ZERO :
                quantityGoal.subtract(actualQuantity);
    }

    private static boolean isActualQuantityEqualOrGreaterThanGoal(BigDecimal actualQuantity, BigDecimal quantityGoal) {
        return actualQuantity.compareTo(quantityGoal) >= 0;
    }
}
