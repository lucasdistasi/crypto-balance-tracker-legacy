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

@Slf4j
@Service
@RequiredArgsConstructor
public class CoingeckoServiceImpl implements CoingeckoService {

    private final WebClient coingeckoWebClient;

    @Override
    @Cacheable(cacheNames = "coingeckoCryptos")
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
    @Cacheable(cacheNames = "cryptoPrice", key = "#coinId")
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
