package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.GoalRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoServiceImplTest {

    @Mock
    Clock clockMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    GoalRepository goalRepositoryMock;

    @Mock
    UserCryptoRepository userCryptoRepositoryMock;

    @Mock
    CoingeckoService coingeckoServiceMock;

    CryptoServiceImpl cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoServiceImpl(clockMock, cryptoRepositoryMock, goalRepositoryMock,
                userCryptoRepositoryMock, coingeckoServiceMock);
    }

    @Test
    void shouldSaveCrypto() {
        var cryptoCaptor = ArgumentCaptor.forClass(Crypto.class);
        var localDateTime = LocalDateTime.of(2023, 5, 3, 18, 55, 0);
        var zonedDateTime = ZonedDateTime.of(2023, 5, 3, 19, 0, 0, 0, ZoneId.of("UTC"));
        var coinInfo = MockData.getBitcoinCoinInfo();

        when(cryptoRepositoryMock.findById("bitcoin")).thenReturn(Optional.empty());
        when(coingeckoServiceMock.retrieveCoinInfo("bitcoin")).thenReturn(coinInfo);
        when(clockMock.instant()).thenReturn(localDateTime.toInstant(ZoneOffset.UTC));
        when(clockMock.getZone()).thenReturn(zonedDateTime.getZone());

        cryptoService.saveCryptoIfNotExists("bitcoin");

        verify(cryptoRepositoryMock, times(1)).save(cryptoCaptor.capture());

        var capturedCrypto = cryptoCaptor.getValue();

        assertAll(
                () -> assertEquals("bitcoin", capturedCrypto.getId()),
                () -> assertEquals("Bitcoin", capturedCrypto.getName()),
                () -> assertEquals("btc", capturedCrypto.getTicker()),
                () -> assertEquals(BigDecimal.valueOf(150000), capturedCrypto.getLastKnownPrice()),
                () -> assertEquals(BigDecimal.valueOf(170000), capturedCrypto.getLastKnownPriceInEUR()),
                () -> assertEquals(BigDecimal.ONE, capturedCrypto.getLastKnownPriceInBTC()),
                () -> assertEquals(BigDecimal.valueOf(1000), capturedCrypto.getCirculatingSupply()),
                () -> assertEquals(BigDecimal.valueOf(1000), capturedCrypto.getMaxSupply()),
                () -> assertEquals(localDateTime, capturedCrypto.getLastPriceUpdatedAt())
        );
    }

    @Test
    void shouldNotSaveCrypto() {
        var cryptoCaptor = ArgumentCaptor.forClass(Crypto.class);
        var crypto = Crypto.builder()
                .id("bitcoin")
                .name("Bitcoin")
                .ticker("BTC")
                .lastKnownPrice(BigDecimal.valueOf(150000))
                .lastKnownPriceInEUR(BigDecimal.valueOf(170000))
                .lastKnownPriceInBTC(BigDecimal.ONE)
                .circulatingSupply(BigDecimal.valueOf(1000))
                .maxSupply(BigDecimal.valueOf(1000))
                .build();

        when(cryptoRepositoryMock.findById("bitcoin")).thenReturn(Optional.of(crypto));

        cryptoService.saveCryptoIfNotExists("bitcoin");

        verify(cryptoRepositoryMock, never()).save(cryptoCaptor.capture());
        verify(coingeckoServiceMock, never()).retrieveCoinInfo("bitcoin");
    }

}