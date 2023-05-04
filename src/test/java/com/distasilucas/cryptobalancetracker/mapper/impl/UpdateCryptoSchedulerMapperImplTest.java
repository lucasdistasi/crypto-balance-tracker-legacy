package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCryptoSchedulerMapperImplTest {

    @Mock
    Clock clockMock;

    @Mock
    CoingeckoService coingeckoServiceMock;

    EntityMapper<Crypto, Crypto> updateCryptoSchedulerMapperImpl;

    @BeforeEach
    void setUp() {
        updateCryptoSchedulerMapperImpl = new UpdateCryptoSchedulerMapperImpl(clockMock, coingeckoServiceMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var coinInfo = MockData.getCoinInfo();
        var cryptoToMap = MockData.getCrypto("1234");
        var localDateTime = LocalDateTime.of(2023, 5, 3, 18, 55, 0);
        var zonedDateTime = ZonedDateTime.of(2023, 5, 3, 19, 0, 0, 0, ZoneId.of("UTC"));

        when(coingeckoServiceMock.retrieveCoinInfo("bitcoin")).thenReturn(coinInfo);
        when(clockMock.instant()).thenReturn(localDateTime.toInstant(ZoneOffset.UTC));
        when(clockMock.getZone()).thenReturn(zonedDateTime.getZone());

        var crypto = updateCryptoSchedulerMapperImpl.mapFrom(cryptoToMap);

        assertAll(
                () -> assertEquals("ABC1234", crypto.getId()),
                () -> assertEquals("Bitcoin", crypto.getName()),
                () -> assertEquals("BTC", crypto.getTicker()),
                () -> assertEquals(BigDecimal.valueOf(1), crypto.getQuantity()),
                () -> assertEquals("1234", crypto.getPlatformId()),
                () -> assertEquals(BigDecimal.valueOf(150_000), crypto.getLastKnownPrice()),
                () -> assertEquals(BigDecimal.valueOf(170_000), crypto.getLastKnownPriceInEUR()),
                () -> assertEquals(BigDecimal.valueOf(1), crypto.getLastKnownPriceInBTC()),
                () -> assertEquals(BigDecimal.valueOf(1000), crypto.getCirculatingSupply()),
                () -> assertEquals(BigDecimal.valueOf(1000), crypto.getMaxSupply()),
                () -> assertEquals(localDateTime, crypto.getLastPriceUpdatedAt())
        );
    }

    @Test
    void shouldReturnSameCryptoIfWebClientResponseExceptionIsThrown() {
        var cryptoToMap = MockData.getCrypto("1234");
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", null, null, null);

        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveCoinInfo("bitcoin");

        var mappedCrypto = updateCryptoSchedulerMapperImpl.mapFrom(cryptoToMap);

        assertAll(
                () -> assertEquals(cryptoToMap.getId(), mappedCrypto.getId()),
                () -> assertEquals(cryptoToMap.getName(), mappedCrypto.getName()),
                () -> assertEquals(cryptoToMap.getTicker(), mappedCrypto.getTicker()),
                () -> assertEquals(cryptoToMap.getQuantity(), mappedCrypto.getQuantity()),
                () -> assertEquals(cryptoToMap.getPlatformId(), mappedCrypto.getPlatformId()),
                () -> assertEquals(cryptoToMap.getLastKnownPrice(), mappedCrypto.getLastKnownPrice()),
                () -> assertEquals(cryptoToMap.getLastKnownPriceInEUR(), mappedCrypto.getLastKnownPriceInEUR()),
                () -> assertEquals(cryptoToMap.getLastKnownPriceInBTC(), mappedCrypto.getLastKnownPriceInBTC()),
                () -> assertEquals(cryptoToMap.getCirculatingSupply(), mappedCrypto.getCirculatingSupply()),
                () -> assertEquals(cryptoToMap.getMaxSupply(), mappedCrypto.getMaxSupply()),
                () -> assertEquals(cryptoToMap.getLastPriceUpdatedAt(), mappedCrypto.getLastPriceUpdatedAt())
        );
    }

    @Test
    void shouldReturnSameCryptoIfNonCaughtExceptionIsThrown() {
        var cryptoToMap = MockData.getCrypto("1234");
        var runtimeException = new RuntimeException("RuntimeException");

        doThrow(runtimeException).when(coingeckoServiceMock).retrieveCoinInfo("bitcoin");

        var mappedCrypto = updateCryptoSchedulerMapperImpl.mapFrom(cryptoToMap);

        assertAll(
                () -> assertEquals(cryptoToMap.getId(), mappedCrypto.getId()),
                () -> assertEquals(cryptoToMap.getName(), mappedCrypto.getName()),
                () -> assertEquals(cryptoToMap.getTicker(), mappedCrypto.getTicker()),
                () -> assertEquals(cryptoToMap.getQuantity(), mappedCrypto.getQuantity()),
                () -> assertEquals(cryptoToMap.getPlatformId(), mappedCrypto.getPlatformId()),
                () -> assertEquals(cryptoToMap.getLastKnownPrice(), mappedCrypto.getLastKnownPrice()),
                () -> assertEquals(cryptoToMap.getLastKnownPriceInEUR(), mappedCrypto.getLastKnownPriceInEUR()),
                () -> assertEquals(cryptoToMap.getLastKnownPriceInBTC(), mappedCrypto.getLastKnownPriceInBTC()),
                () -> assertEquals(cryptoToMap.getCirculatingSupply(), mappedCrypto.getCirculatingSupply()),
                () -> assertEquals(cryptoToMap.getMaxSupply(), mappedCrypto.getMaxSupply()),
                () -> assertEquals(cryptoToMap.getLastPriceUpdatedAt(), mappedCrypto.getLastPriceUpdatedAt())
        );
    }
}