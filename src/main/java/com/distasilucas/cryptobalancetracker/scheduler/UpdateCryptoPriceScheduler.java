package com.distasilucas.cryptobalancetracker.scheduler;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class UpdateCryptoPriceScheduler {

    private final int maxLimit;
    private final Clock clock;
    private final CryptoRepository cryptoRepository;
    private final EntityMapper<Crypto, Crypto> updateCryptoSchedulerMapperImpl;

    public UpdateCryptoPriceScheduler(@Value("${max-limit-crypto}") int maxLimit,
                                      Clock clock,
                                      CryptoRepository cryptoRepository,
                                      EntityMapper<Crypto, Crypto> updateCryptoSchedulerMapperImpl) {
        this.maxLimit = maxLimit;
        this.clock = clock;
        this.cryptoRepository = cryptoRepository;
        this.updateCryptoSchedulerMapperImpl = updateCryptoSchedulerMapperImpl;
    }

    @Scheduled(cron = "0 */3 * ? * *")
    public void updateCryptosMarketData() {
        log.info("Running cron to update cryptos data...");

        List<Crypto> cryptosToUpdate = getCryptosToUpdate()
                .stream()
                .map(updateCryptoSchedulerMapperImpl::mapFrom)
                .toList();

        cryptoRepository.saveAll(cryptosToUpdate);
    }

    private List<Crypto> getCryptosToUpdate() {
        LocalDateTime lastUpdatedPrice = LocalDateTime.now(clock).minusMinutes(5);

        List<String> cryptosIdToUpdate = cryptoRepository.findTopNCryptosOrderByLastPriceUpdatedAtAsc(lastUpdatedPrice, maxLimit)
                .stream()
                .map(Crypto::getId)
                .toList();

        return cryptoRepository.findAllById(cryptosIdToUpdate);
    }
}
