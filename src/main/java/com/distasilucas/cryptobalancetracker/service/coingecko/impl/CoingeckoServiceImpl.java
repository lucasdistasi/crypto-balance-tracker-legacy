package com.distasilucas.cryptobalancetracker.service.coingecko.impl;

import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COINGECKO_CRYPTOS_CACHE;
import static com.distasilucas.cryptobalancetracker.constant.Constants.CRYPTO_PRICE_CACHE;

@Slf4j
@Service
public class CoingeckoServiceImpl implements CoingeckoService {

    private final String coingeckoApiKey;
    private final WebClient coingeckoWebClient;

    public CoingeckoServiceImpl(@Value("${coingecko.pro.api-key}") String coingeckoApiKey,
                                WebClient coingeckoWebClient) {
        this.coingeckoApiKey = coingeckoApiKey;
        this.coingeckoWebClient = coingeckoWebClient;
    }

    @Override
    @Cacheable(cacheNames = COINGECKO_CRYPTOS_CACHE)
    @Retryable(retryFor = { WebClientException.class }, backoff = @Backoff(delay = 1500))
    public List<Coin> retrieveAllCoins() {
        log.info("Hitting Coingecko API... Retrieving all cryptos");

        return coingeckoWebClient.get()
                .uri(getUriBuilder("/coins/list"))
                .retrieve()
                .bodyToFlux(Coin.class)
                .collectList()
                .block();
    }

    @Override
    @Cacheable(cacheNames = CRYPTO_PRICE_CACHE, key = "#coinId")
    @Retryable(retryFor = { WebClientException.class }, backoff = @Backoff(delay = 1500))
    public CoinInfo retrieveCoinInfo(String coinId) {
        log.info("Hitting Coingecko API... Retrieving information for [{}]", coinId);
        String uri = String.format("/coins/%s", coinId);

        CoinInfo coinInfo = coingeckoWebClient.get()
                .uri(getUriBuilder(uri))
                .retrieve()
                .bodyToMono(CoinInfo.class)
                .block();

        return mapCoinInfo().apply(coinInfo);
    }

    private Function<UriBuilder, URI> getUriBuilder(String url) {
        Function<UriBuilder, URI> proCoingekoUri = uriBuilder -> uriBuilder.path(url)
                .queryParam("x_cg_pro_api_key", coingeckoApiKey)
                .build();

        Function<UriBuilder, URI> freeCoingekoUri = uriBuilder -> uriBuilder.path(url)
                .build();

        return StringUtils.isNotBlank(coingeckoApiKey) ?
                proCoingekoUri :
                freeCoingekoUri;
    }

    private Function<CoinInfo, CoinInfo> mapCoinInfo() {
        return originalCoinInfo -> {
            CoinInfo coinInfo = new CoinInfo();
            coinInfo.setId(originalCoinInfo.getId());
            coinInfo.setName(originalCoinInfo.getName());
            coinInfo.setSymbol(originalCoinInfo.getSymbol());
            coinInfo.setMarketData(originalCoinInfo.getMarketData());

            return coinInfo;
        };
    }
}
