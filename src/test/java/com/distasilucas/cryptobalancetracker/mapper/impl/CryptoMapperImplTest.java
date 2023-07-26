package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
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

import java.math.BigDecimal;
import java.util.Collections;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
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

    EntityMapper<UserCrypto, AddCryptoRequest> entityMapper;

    @BeforeEach
    void setUp() {
        entityMapper = new CryptoMapperImpl(coingeckoServiceMock, platformServiceMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var addCryptoRequest = new AddCryptoRequest("Bitcoin", BigDecimal.valueOf(1), "Ledger");
        var platformName = addCryptoRequest.getPlatform();
        var platform = Platform.builder()
                .id("1234")
                .name(platformName)
                .build();
        var allCoins = MockData.getAllCoins("Bitcoin", "bitcoin");
        var coinInfo = MockData.getBitcoinCoinInfo();

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);
        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);

        var crypto = entityMapper.mapFrom(addCryptoRequest);

        assertAll(
                () -> assertEquals(addCryptoRequest.getQuantity(), crypto.getQuantity()),
                () -> assertEquals(platform.getId(), crypto.getPlatformId()),
                () -> assertEquals(coinInfo.getId(), crypto.getCryptoId())
        );
    }

    @Test
    void shouldThrowExceptionWhenMappingNonExistentCoin() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platformName = addCryptoRequest.getPlatform();
        var platform = MockData.getPlatform(platformName);

        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.emptyList());

        var coinNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> entityMapper.mapFrom(addCryptoRequest));

        var expectedMessage = String.format(CRYPTO_NAME_NOT_FOUND, addCryptoRequest.getCryptoName());

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
}