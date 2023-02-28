package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.MockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.Constants.MAX_RATE_LIMIT_REACHED;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN_ERROR;
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

    EntityMapper<Crypto, CryptoDTO> entityMapper;

    @BeforeEach
    void setUp() {
        entityMapper = new CryptoMapperImpl(coingeckoServiceMock, platformServiceMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platformName = cryptoDTO.platform();
        var platform = Platform.builder()
                .id("1234")
                .name(platformName)
                .build();
        var allCoins = MockData.getAllCoins();

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);

        var crypto = entityMapper.mapFrom(cryptoDTO);

        assertAll(
                () -> assertEquals(cryptoDTO.coin_name(), crypto.getName()),
                () -> assertEquals(cryptoDTO.coinId(), crypto.getCoinId()),
                () -> assertEquals(cryptoDTO.ticker(), crypto.getTicker()),
                () -> assertEquals(cryptoDTO.quantity(), crypto.getQuantity()),
                () -> assertEquals(platform.getId(), crypto.getPlatformId())
        );
    }

    @Test
    void shouldThrowExceptionWhenMappingNonExistentCoin() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platformName = cryptoDTO.platform();
        var platform = MockData.getPlatform(platformName);

        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.emptyList());

        CoinNotFoundException coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> entityMapper.mapFrom(cryptoDTO));

        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, cryptoDTO.coin_name());

        assertEquals(expectedMessage, coinNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowApiExceptionWhenReachingRateLimit() {
        var cryptoDTO = MockData.getCryptoDTO();
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", null, null, null);

        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveAllCoins();

        ApiException apiException = assertThrows(ApiException.class,
                () -> entityMapper.mapFrom(cryptoDTO));

        assertAll(
                () -> assertEquals(HttpStatus.TOO_MANY_REQUESTS, apiException.getHttpStatusCode()),
                () -> assertEquals(MAX_RATE_LIMIT_REACHED, apiException.getMessage())
        );
    }

    @Test
    void shouldThrowApiException() {
        var cryptoDTO = MockData.getCryptoDTO();
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_EARLY.value(), "TOO_EARLY", null, null, null);

        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveAllCoins();

        ApiException apiException = assertThrows(ApiException.class,
                () -> entityMapper.mapFrom(cryptoDTO));

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiException.getHttpStatusCode()),
                () -> assertEquals(UNKNOWN_ERROR, apiException.getMessage())
        );
    }
}