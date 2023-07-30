package com.distasilucas.cryptobalancetracker.service.coingecko.impl;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCrypto;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCryptoInfo;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
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
    public List<CoingeckoCrypto> retrieveAllCoingeckoCryptos() {
        log.info("Hitting Coingecko API... Retrieving all cryptos");

        return coingeckoWebClient.get()
                .uri(getAllCoinsUriBuilder("/coins/list"))
                .retrieve()
                .bodyToFlux(CoingeckoCrypto.class)
                .collectList()
                .block();
    }

    @Override
    @Cacheable(cacheNames = CRYPTO_PRICE_CACHE, key = "#coinId")
    @Retryable(retryFor = { WebClientException.class }, backoff = @Backoff(delay = 1500))
    public CoingeckoCryptoInfo retrieveCoingeckoCryptoInfo(String coinId) {
        log.info("Hitting Coingecko API... Retrieving information for [{}]", coinId);
        String uri = String.format("/coins/%s", coinId);

        return coingeckoWebClient.get()
                .uri(getCoingeckoCryptoInfoUriBuilder(uri))
                .retrieve()
                .bodyToMono(CoingeckoCryptoInfo.class)
                .block();
    }

    private Function<UriBuilder, URI> getAllCoinsUriBuilder(String url) {
        Function<UriBuilder, URI> proCoingeckoUri = uriBuilder -> uriBuilder.path(url)
                .queryParam("x_cg_pro_api_key", coingeckoApiKey)
                .build();

        Function<UriBuilder, URI> freeCoingekoUri = uriBuilder -> uriBuilder.path(url)
                .build();

        return StringUtils.isNotBlank(coingeckoApiKey) ?
                proCoingeckoUri :
                freeCoingekoUri;
    }

    private Function<UriBuilder, URI> getCoingeckoCryptoInfoUriBuilder(String url) {
        MultiValueMap<String, String> commonParams = new HttpHeaders();
        commonParams.add("tickers", Boolean.FALSE.toString());
        commonParams.add("community_data", Boolean.FALSE.toString());
        commonParams.add("developer_data", Boolean.FALSE.toString());

        Function<UriBuilder, URI> proCoingekoUri = uriBuilder -> uriBuilder.path(url)
                .queryParam("x_cg_pro_api_key", coingeckoApiKey)
                .queryParams(commonParams)
                .build();

        Function<UriBuilder, URI> freeCoingekoUri = uriBuilder -> uriBuilder.path(url)
                .queryParams(commonParams)
                .build();

        return StringUtils.isNotBlank(coingeckoApiKey) ?
                proCoingekoUri :
                freeCoingekoUri;
    }
}
