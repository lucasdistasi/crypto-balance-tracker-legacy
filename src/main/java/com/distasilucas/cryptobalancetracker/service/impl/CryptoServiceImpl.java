package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CryptoServiceImpl {

    private final Clock clock;
    private final CryptoRepository cryptoRepository;
    private final CoingeckoService coingeckoService;

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

    // TODO
    public void deleteCryptoIfNotUsed(String cryptoId) {

    }
}
