package com.distasilucas.cryptobalancetracker.service.coingecko.impl;

import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CoingeckoServiceImplTest {

    public static MockWebServer mockBackEnd;
    private CoingeckoService coingeckoService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void setUpService() {
        WebClient webClient = WebClient.create("https://api.coingecko.com/api/v3");
        coingeckoService = new CoingeckoServiceImpl("", webClient);
    }

    @Test
    void shouldRetrieveAllCoins() {
        var mockResponse = new MockResponse();
        mockBackEnd.enqueue(mockResponse);

        List<Coin> coins = coingeckoService.retrieveAllCoins();

        StepVerifier.create(Flux.just(coins))
                .expectNextMatches(CollectionUtils::isNotEmpty)
                .verifyComplete();
    }

    @Test
    void shouldRetrieveCoinInfo() {
        var mockResponse = new MockResponse();
        mockBackEnd.enqueue(mockResponse);

        CoinInfo coins = coingeckoService.retrieveCoinInfo("bitcoin");

        StepVerifier.create(Mono.just(coins))
                .expectNextMatches(coinInfo -> coinInfo.getName().equals("Bitcoin"))
                .verifyComplete();
    }

    @Test
    void shouldThrowWebClientResponseExceptionForInvalidApiKey() {
        var mockResponse = new MockResponse();
        mockBackEnd.enqueue(mockResponse);
        WebClient webClient = WebClient.create("https://pro-api.coingecko.com/api/v3");
        coingeckoService = new CoingeckoServiceImpl("ABC123", webClient);

        assertThrows(WebClientResponseException.class, () -> coingeckoService.retrieveCoinInfo("bitcoin"));
    }

}