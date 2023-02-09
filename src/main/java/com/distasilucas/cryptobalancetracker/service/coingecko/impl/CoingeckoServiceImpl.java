package com.distasilucas.cryptobalancetracker.service.coingecko.impl;

import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COINGECKO_CRYPTOS_CACHE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.CRYPTO_PRICE_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoingeckoServiceImpl implements CoingeckoService {

    private final WebClient coingeckoWebClient;

    @Override
    @Cacheable(cacheNames = COINGECKO_CRYPTOS_CACHE)
    public List<Coin> retrieveAllCoins() {
        log.info("Retrieving all cryptos from Coingecko");

        return coingeckoWebClient.get()
                .uri("/coins/list")
                .retrieve()
                .bodyToFlux(Coin.class)
                .collectList()
                .block();
    }

    @Override
    @Cacheable(cacheNames = CRYPTO_PRICE_CACHE, key = "#coinId")
    public CoinInfo retrieveCoinInfo(String coinId) {
        log.info("Retrieving information for {}", coinId);
        String uri = String.format("/coins/%s", coinId);

        return coingeckoWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CoinInfo.class)
                .block();
    }
}
