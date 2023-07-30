package com.distasilucas.cryptobalancetracker.service.coingecko.impl;

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
        var webClient = WebClient.create("https://api.coingecko.com/api/v3");
        coingeckoService = new CoingeckoServiceImpl("", webClient);
    }

    @Test
    void shouldRetrieveAllCoins() {
        var mockResponse = new MockResponse();
        mockBackEnd.enqueue(mockResponse);

        var coins = coingeckoService.retrieveAllCoingeckoCryptos();

        StepVerifier.create(Flux.just(coins))
                .expectNextMatches(CollectionUtils::isNotEmpty)
                .verifyComplete();
    }

    @Test
    void shouldRetrieveCoingeckoCryptoInfo() {
        var mockResponse = new MockResponse();
        mockBackEnd.enqueue(mockResponse);

        var coingeckoCryptoInfo = coingeckoService.retrieveCoingeckoCryptoInfo("bitcoin");

        StepVerifier.create(Mono.just(coingeckoCryptoInfo))
                .expectNextMatches(cryptoInfo -> cryptoInfo.getName().equals("Bitcoin"))
                .verifyComplete();
    }

    @Test
    void shouldThrowWebClientResponseExceptionForInvalidApiKey() {
        var mockResponse = new MockResponse();
        mockBackEnd.enqueue(mockResponse);
        var webClient = WebClient.create("https://pro-api.coingecko.com/api/v3");
        coingeckoService = new CoingeckoServiceImpl("ABC123", webClient);

        assertThrows(WebClientResponseException.class, () -> coingeckoService.retrieveCoingeckoCryptoInfo("bitcoin"));
    }

}