package com.distasilucas.cryptobalancetracker.scheduler;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateCryptoPriceScheduler {

    private final Clock clock;
    private final CryptoRepository cryptoRepository;
    private final EntityMapper<Crypto, Crypto> updateCryptoSchedulerMapperImpl;

    @Scheduled(cron = "0 */3 * ? * *")
    public void updateCryptosMarketData() {
        int maxLimit = 8;
        LocalDateTime lastUpdatedPrice = LocalDateTime.now(clock).minusMinutes(5);
        Set<String> lastMaxLimitCryptos = getLatestCryptosIds(lastUpdatedPrice, maxLimit);
        Set<String> cryptosToUpdate = lastMaxLimitCryptos.size() <= maxLimit ? lastMaxLimitCryptos : getLatestCryptosIds(lastUpdatedPrice, 5);

        cryptosToUpdate.forEach(coinId -> {
            log.info("Running cron to update last known price for [{}]", coinId);
            cryptoRepository.findAllByCoinId(coinId)
                    .ifPresent(cryptos -> {
                        log.info("Updating [{}] occurrences of [{}]", cryptos.size(), coinId);

                        List<Crypto> updatedCryptos = cryptos.stream()
                                .map(updateCryptoSchedulerMapperImpl::mapFrom)
                                .toList();

                        cryptoRepository.saveAll(updatedCryptos);
                    });
        });
    }

    private Set<String> getLatestCryptosIds(LocalDateTime lastUpdatedPrice, int limit) {
        return cryptoRepository.findTopNCryptosOrderByLastPriceUpdatedAtAsc(lastUpdatedPrice, limit)
                .stream()
                .map(Crypto::getCoinId)
                .collect(Collectors.toSet());
    }
}
