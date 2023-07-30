package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Goal;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void shouldFindAllById() {
        var crypto = Crypto.builder()
                .id("bitcoin")
                .build();

        when(cryptoRepositoryMock.findAllById(Collections.singletonList("bitcoin")))
                .thenReturn(Collections.singletonList(crypto));

        var cryptos = cryptoService.findAllById(Collections.singletonList("bitcoin"));

        assertFalse(cryptos.isEmpty());
        assertEquals(crypto, cryptos.get(0));
    }

    @Test
    void shouldSaveAllCryptos() {
        var crypto = Crypto.builder()
                .id("bitcoin")
                .build();
        var cryptos = Collections.singletonList(crypto);

        when(cryptoRepositoryMock.saveAll(cryptos))
                .thenReturn(cryptos);

        cryptoService.saveAllCryptos(cryptos);

        verify(cryptoRepositoryMock, times(1)).saveAll(cryptos);
    }

    @Test
    void shouldSaveCrypto() {
        var cryptoCaptor = ArgumentCaptor.forClass(Crypto.class);
        var localDateTime = LocalDateTime.of(2023, 5, 3, 18, 55, 0);
        var zonedDateTime = ZonedDateTime.of(2023, 5, 3, 19, 0, 0, 0, ZoneId.of("UTC"));
        var coingeckoCryptoInfo = MockData.getBitcoinCoingeckoCryptoInfo();

        when(cryptoRepositoryMock.findById("bitcoin")).thenReturn(Optional.empty());
        when(coingeckoServiceMock.retrieveCoingeckoCryptoInfo("bitcoin")).thenReturn(coingeckoCryptoInfo);
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
        verify(coingeckoServiceMock, never()).retrieveCoingeckoCryptoInfo("bitcoin");
    }

    @Test
    void shouldNotDeleteCryptoIfUsed() {
        var goal = Goal.builder()
                .cryptoId("bitcoin")
                .build();
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .build();
        var crypto = Crypto.builder()
                .id("bitcoin")
                .build();

        when(goalRepositoryMock.findByCryptoId("bitcoin"))
                .thenReturn(Optional.of(goal));
        when(userCryptoRepositoryMock.findFirstByCryptoId("bitcoin"))
                .thenReturn(Optional.of(userCrypto));

        cryptoService.deleteCryptoIfNotUsed("bitcoin");

        verify(cryptoRepositoryMock, times(0)).delete(crypto);
    }

    @Test
    void shouldDeleteCryptoIfNotUsed() {
        var crypto = Crypto.builder()
                .id("bitcoin")
                .build();

        when(goalRepositoryMock.findByCryptoId("bitcoin"))
                .thenReturn(Optional.empty());
        when(userCryptoRepositoryMock.findFirstByCryptoId("bitcoin"))
                .thenReturn(Optional.empty());
        when(cryptoRepositoryMock.findById("bitcoin"))
                .thenReturn(Optional.of(crypto));

        cryptoService.deleteCryptoIfNotUsed("bitcoin");

        verify(cryptoRepositoryMock, times(1)).delete(crypto);
    }

    @Test
    void shouldFindTopCryptos() {
        var crypto = Crypto.builder()
                .id("bitcoin")
                .build();
        var localDate = LocalDateTime.now();

        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(localDate, 5))
                .thenReturn(Collections.singletonList(crypto));

        var cryptos = cryptoService.findTopNCryptosOrderByLastPriceUpdatedAtAsc(localDate, 5);

        assertFalse(cryptos.isEmpty());
        assertEquals(crypto, cryptos.get(0));
    }

}