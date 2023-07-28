package com.distasilucas.cryptobalancetracker.scheduler;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCryptoPriceSchedulerTest {

    @Mock
    Clock clock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    EntityMapper<Crypto, Crypto> updateCryptoSchedulerMapperImplMock;

    UpdateCryptoPriceScheduler updateCryptoPriceScheduler;

    @BeforeEach
    void setUp() {
        updateCryptoPriceScheduler = new UpdateCryptoPriceScheduler(9, clock, cryptoRepositoryMock, updateCryptoSchedulerMapperImplMock);
    }

    @Test
    void shouldExecuteScheduler() {
        var localDateMinusFiveMinutes = LocalDateTime.of(2023, 5, 3, 18, 55, 0);
        var zonedDateTime = ZonedDateTime.of(2023, 5, 3, 19, 0, 0, 0, ZoneId.of("UTC"));
        var cryptos = MockData.getAllCryptos();

        when(clock.getZone()).thenReturn(zonedDateTime.getZone());
        when(clock.instant()).thenReturn(zonedDateTime.toInstant());
        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(localDateMinusFiveMinutes, 9))
                .thenReturn(cryptos);
        when(cryptoRepositoryMock.findAllById(Collections.singletonList("bitcoin"))).thenReturn(cryptos);
        when(updateCryptoSchedulerMapperImplMock.mapFrom(cryptos.get(0))).thenReturn(cryptos.get(0));

        updateCryptoPriceScheduler.updateCryptosMarketData();

        verify(cryptoRepositoryMock, times(1)).saveAll(cryptos);
    }
}