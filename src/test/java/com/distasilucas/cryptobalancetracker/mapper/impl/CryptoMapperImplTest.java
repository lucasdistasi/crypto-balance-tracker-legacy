package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.crypto.CryptoMapperImpl;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.MAX_RATE_LIMIT_REACHED;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoMapperImplTest {

    @Mock
    CoingeckoService coingeckoServiceMock;

    @Mock
    PlatformService platformServiceMock;

    EntityMapper<Crypto, AddCryptoRequest> entityMapper;

    @BeforeEach
    void setUp() {
        entityMapper = new CryptoMapperImpl(coingeckoServiceMock, platformServiceMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platformName = addCryptoRequest.getPlatform();
        var platform = Platform.builder()
                .id("1234")
                .name(platformName)
                .build();
        var allCoins = MockData.getAllCoins();
        var coinInfo = MockData.getCoinInfo();

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        when(coingeckoServiceMock.retrieveCoinInfo("ethereum")).thenReturn(coinInfo);

        var crypto = entityMapper.mapFrom(addCryptoRequest);

        assertAll(
                () -> assertEquals(addCryptoRequest.getCoinName(), crypto.getName()),
                () -> assertEquals(addCryptoRequest.getQuantity(), crypto.getQuantity()),
                () -> assertEquals(platform.getId(), crypto.getPlatformId()),
                () -> assertEquals(coinInfo.getMarketData().currentPrice().usd(), crypto.getLastKnownPrice()),
                () -> assertEquals(coinInfo.getMarketData().currentPrice().eur(), crypto.getLastKnownPriceInEUR()),
                () -> assertEquals(coinInfo.getMarketData().currentPrice().btc(), crypto.getLastKnownPriceInBTC())
        );
    }

    @Test
    void shouldThrowExceptionWhenMappingNonExistentCoin() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platformName = addCryptoRequest.getPlatform();
        var platform = MockData.getPlatform(platformName);

        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.emptyList());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> entityMapper.mapFrom(addCryptoRequest));

        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, addCryptoRequest.getCoinName());

        assertEquals(expectedMessage, coinNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowApiExceptionWhenReachingRateLimit() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", null, null, null);

        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveAllCoins();

        var apiException = assertThrows(ApiException.class,
                () -> entityMapper.mapFrom(addCryptoRequest));

        assertAll(
                () -> assertEquals(HttpStatus.TOO_MANY_REQUESTS, apiException.getHttpStatusCode()),
                () -> assertEquals(MAX_RATE_LIMIT_REACHED, apiException.getMessage())
        );
    }

    @Test
    void shouldThrowApiExceptionWhenCallingRetrieveAllCoins() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_EARLY.value(), "TOO_EARLY", null, null, null);

        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveAllCoins();

        var apiException = assertThrows(ApiException.class,
                () -> entityMapper.mapFrom(addCryptoRequest));

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiException.getHttpStatusCode()),
                () -> assertEquals(UNKNOWN_ERROR, apiException.getMessage())
        );
    }

    @Test
    void shouldThrowApiExceptionWhenCallingRetrieveCoinInfo() {
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_EARLY.value(), "TOO_EARLY", null, null, null);
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platformName = addCryptoRequest.getPlatform();
        var platform = Platform.builder()
                .id("1234")
                .name(platformName)
                .build();
        var allCoins = MockData.getAllCoins();

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveCoinInfo(allCoins.get(0).getId());

        var apiException = assertThrows(ApiException.class,
                () -> entityMapper.mapFrom(addCryptoRequest));

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiException.getHttpStatusCode()),
                () -> assertEquals(UNKNOWN_ERROR, apiException.getMessage())
        );
    }

    @Test
    void shouldThrowApiExceptionWhenReachingRateLimitForRetrieveCoinInfo() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", null, null, null);
        var platformName = addCryptoRequest.getPlatform();
        var platform = Platform.builder()
                .id("1234")
                .name(platformName)
                .build();
        var allCoins = MockData.getAllCoins();

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveCoinInfo(allCoins.get(0).getId());

        var apiException = assertThrows(ApiException.class,
                () -> entityMapper.mapFrom(addCryptoRequest));

        assertAll(
                () -> assertEquals(HttpStatus.TOO_MANY_REQUESTS, apiException.getHttpStatusCode()),
                () -> assertEquals(MAX_RATE_LIMIT_REACHED, apiException.getMessage())
        );
    }
}