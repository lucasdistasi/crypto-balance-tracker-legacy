package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.MockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import static com.distasilucas.cryptobalancetracker.constant.Constants.MAX_RATE_LIMIT_REACHED;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoBalanceResponseMapperImplTest {

    @Mock
    CoingeckoService coingeckoServiceImplMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoBalanceResponseMapperImpl = new CryptoBalanceResponseMapperImpl(coingeckoServiceImplMock, platformRepositoryMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var cryptos = MockData.getAllCryptos();
        var coinInfo = MockData.getCoinInfo();

        when(coingeckoServiceImplMock.retrieveCoinInfo("bitcoin")).thenReturn(coinInfo);

        var cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(cryptos);
        var totalBalance = cryptoBalanceResponse.getTotalBalance();
        var expectedBalance = totalBalance.setScale(2, RoundingMode.HALF_UP);

        assertAll(
                () -> assertEquals(cryptos.size(), cryptoBalanceResponse.getCoins().size()),
                () -> assertEquals(expectedBalance, totalBalance)
        );
    }

    @Test
    void shouldThrowApiExceptionWhenReachingRateLimit() {
        var crypto = MockData.getCrypto("Ledger");
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", null, null, null);

        doThrow(webClientResponseException).when(coingeckoServiceImplMock).retrieveCoinInfo(crypto.getCoinId());

        ApiException apiException = assertThrows(ApiException.class,
                () -> cryptoBalanceResponseMapperImpl.mapFrom(Collections.singletonList(crypto)));

        assertAll(
                () -> assertEquals(HttpStatus.TOO_MANY_REQUESTS, apiException.getHttpStatusCode()),
                () -> assertEquals(MAX_RATE_LIMIT_REACHED, apiException.getMessage())
        );
    }

    @Test
    void shouldThrowApiException() {
        var crypto = MockData.getCrypto("Ledger");
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_EARLY.value(), "TOO_EARLY", null, null, null);

        doThrow(webClientResponseException).when(coingeckoServiceImplMock).retrieveCoinInfo(crypto.getCoinId());

        ApiException apiException = assertThrows(ApiException.class,
                () -> cryptoBalanceResponseMapperImpl.mapFrom(Collections.singletonList(crypto)));

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiException.getHttpStatusCode()),
                () -> assertEquals(UNKNOWN_ERROR, apiException.getMessage())
        );
    }
}