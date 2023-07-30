package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCryptoInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final Clock clock;
    private final CryptoRepository cryptoRepository;
    private final GoalRepository goalRepository;
    private final UserCryptoRepository userCryptoRepository;
    private final CoingeckoService coingeckoService;

    @Override
    public Optional<Crypto> findById(String cryptoId) {
        return cryptoRepository.findById(cryptoId);
    }

    @Override
    public List<Crypto> findAllById(List<String> cryptoIds) {
        return cryptoRepository.findAllById(cryptoIds);
    }

    @Override
    public void saveAllCryptos(List<Crypto> cryptosToSave) {
        log.info("Saving or updating {} cryptos", cryptosToSave.size());
        cryptoRepository.saveAll(cryptosToSave);
    }

    @Override
    public void saveCryptoIfNotExists(String cryptoId) {
        Optional<Crypto> optionalCrypto = cryptoRepository.findById(cryptoId);

        if (optionalCrypto.isEmpty()) {
            CoingeckoCryptoInfo coingeckoCryptoInfo = coingeckoService.retrieveCoingeckoCryptoInfo(cryptoId);
            MarketData marketData = coingeckoCryptoInfo.getMarketData();

            Crypto crypto = Crypto.builder()
                    .id(coingeckoCryptoInfo.getId())
                    .name(coingeckoCryptoInfo.getName())
                    .ticker(coingeckoCryptoInfo.getSymbol())
                    .lastKnownPrice(marketData.currentPrice().usd())
                    .lastKnownPriceInEUR(marketData.currentPrice().eur())
                    .lastKnownPriceInBTC(marketData.currentPrice().btc())
                    .circulatingSupply(marketData.circulatingSupply())
                    .maxSupply(marketData.maxSupply())
                    .lastPriceUpdatedAt(LocalDateTime.now(clock))
                    .build();

            cryptoRepository.save(crypto);
            log.info("Saving Crypto {}", crypto.getName());
        }
    }

    @Override
    public void deleteCryptoIfNotUsed(String cryptoId) {
        Optional<Goal> optionalGoal = goalRepository.findByCryptoId(cryptoId);
        Optional<UserCrypto> optionalUserCrypto = userCryptoRepository.findFirstByCryptoId(cryptoId);

        if (optionalGoal.isEmpty() && optionalUserCrypto.isEmpty()) {
            findById(cryptoId).ifPresent(crypto -> {
                log.info("Deleting Crypto {} because is not being used", crypto.getName());
                cryptoRepository.delete(crypto);
            });
        }
    }

    @Override
    public List<Crypto> findTopNCryptosOrderByLastPriceUpdatedAtAsc(LocalDateTime dateFilter, int limit) {
        return cryptoRepository.findTopNCryptosOrderByLastPriceUpdatedAtAsc(dateFilter, limit);
    }
}
