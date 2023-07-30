package com.distasilucas.cryptobalancetracker.scheduler;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
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
    private final CryptoService cryptoService;
    private final EntityMapper<Crypto, Crypto> updateCryptoSchedulerMapperImpl;

    public UpdateCryptoPriceScheduler(@Value("${max-limit-crypto}") int maxLimit,
                                      Clock clock,
                                      CryptoService cryptoService,
                                      EntityMapper<Crypto, Crypto> updateCryptoSchedulerMapperImpl) {
        this.maxLimit = maxLimit;
        this.clock = clock;
        this.cryptoService = cryptoService;
        this.updateCryptoSchedulerMapperImpl = updateCryptoSchedulerMapperImpl;
    }

    @Scheduled(cron = "0 */3 * ? * *")
    public void updateCryptosMarketData() {
        log.info("Running cron to update cryptos data...");

        List<Crypto> cryptosToUpdate = getCryptosToUpdate()
                .stream()
                .map(updateCryptoSchedulerMapperImpl::mapFrom)
                .toList();

        cryptoService.saveAllCryptos(cryptosToUpdate);
    }

    private List<Crypto> getCryptosToUpdate() {
        LocalDateTime lastUpdatedPrice = LocalDateTime.now(clock).minusMinutes(5);

        List<String> cryptosIdToUpdate = cryptoService.findTopNCryptosOrderByLastPriceUpdatedAtAsc(lastUpdatedPrice, maxLimit)
                .stream()
                .map(Crypto::getId)
                .toList();

        return cryptoService.findAllById(cryptosIdToUpdate);
    }
}
