package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        cryptoRepository.saveAll(cryptosToSave);
    }

    @Override
    public void saveCryptoIfNotExists(String cryptoId) {
        Optional<Crypto> optionalCrypto = cryptoRepository.findById(cryptoId);

        if (optionalCrypto.isEmpty()) {
            CoinInfo coinInfo = coingeckoService.retrieveCoinInfo(cryptoId);
            MarketData marketData = coinInfo.getMarketData();

            Crypto crypto = Crypto.builder()
                    .id(coinInfo.getId())
                    .name(coinInfo.getName())
                    .ticker(coinInfo.getSymbol())
                    .lastKnownPrice(marketData.currentPrice().usd())
                    .lastKnownPriceInEUR(marketData.currentPrice().eur())
                    .lastKnownPriceInBTC(marketData.currentPrice().btc())
                    .circulatingSupply(marketData.circulatingSupply())
                    .maxSupply(marketData.maxSupply())
                    .lastPriceUpdatedAt(LocalDateTime.now(clock))
                    .build();

            cryptoRepository.save(crypto);
        }
    }

    @Override
    public void deleteCryptoIfNotUsed(String cryptoId) {
        Optional<Goal> optionalGoal = goalRepository.findByCryptoId(cryptoId);
        Optional<UserCrypto> optionalUserCrypto = userCryptoRepository.findFirstByCryptoId(cryptoId);

        if (optionalGoal.isEmpty() && optionalUserCrypto.isEmpty()) {
            findById(cryptoId).ifPresent(cryptoRepository::delete);
        }
    }

    @Override
    public List<Crypto> findTopNCryptosOrderByLastPriceUpdatedAtAsc(LocalDateTime dateFilter, int limit) {
        return cryptoRepository.findTopNCryptosOrderByLastPriceUpdatedAtAsc(dateFilter, limit);
    }
}
