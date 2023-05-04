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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        updateCryptoPriceScheduler = new UpdateCryptoPriceScheduler(clock, cryptoRepositoryMock, updateCryptoSchedulerMapperImplMock);
    }

    @Test
    void shouldExecuteSchedulerWithMinLimit() {
        var localDateMinusFiveMinutes = LocalDateTime.of(2023, 5, 3, 18, 55, 0);
        var zonedDateTime = ZonedDateTime.of(2023, 5, 3, 19, 0, 0, 0, ZoneId.of("UTC"));
        var cryptos = MockData.getAllCryptos();

        when(clock.getZone()).thenReturn(zonedDateTime.getZone());
        when(clock.instant()).thenReturn(zonedDateTime.toInstant());
        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(localDateMinusFiveMinutes, 8)).thenReturn(cryptos);
        when(cryptoRepositoryMock.findAllByCoinId("bitcoin")).thenReturn(Optional.of(cryptos));
        when(updateCryptoSchedulerMapperImplMock.mapFrom(cryptos.get(0))).thenReturn(cryptos.get(0));

        updateCryptoPriceScheduler.updateCryptosMarketData();

        verify(updateCryptoSchedulerMapperImplMock, times(1)).mapFrom(cryptos.get(0));
        verify(cryptoRepositoryMock, times(1)).saveAll(cryptos);
    }

    @Test
    void shouldExecuteSchedulerWithMaxLimit() {
        var localDateMinusFiveMinutes = LocalDateTime.of(2023, 5, 3, 18, 55, 0);
        var zonedDateTime = ZonedDateTime.of(2023, 5, 3, 19, 0, 0, 0, ZoneId.of("UTC"));
        var cryptos = MockData.getAllCryptos();
        var maxCryptos = getMaxCryptos();

        when(clock.getZone()).thenReturn(zonedDateTime.getZone());
        when(clock.instant()).thenReturn(zonedDateTime.toInstant());
        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(localDateMinusFiveMinutes, 8)).thenReturn(maxCryptos);
        when(cryptoRepositoryMock.findAllByCoinId(any())).thenReturn(Optional.of(cryptos));
        when(updateCryptoSchedulerMapperImplMock.mapFrom(cryptos.get(0))).thenReturn(cryptos.get(0));

        updateCryptoPriceScheduler.updateCryptosMarketData();

        verify(cryptoRepositoryMock, times(7)).saveAll(cryptos);
    }

    @Test
    void shouldExecuteSchedulerWithMinLimitWhenOccurrencesArGreaterThanMaxLimit() {
        var localDateMinusFiveMinutes = LocalDateTime.of(2023, 5, 3, 18, 55, 0);
        var zonedDateTime = ZonedDateTime.of(2023, 5, 3, 19, 0, 0, 0, ZoneId.of("UTC"));
        var cryptos = MockData.getAllCryptos();
        var maxCryptos = getMaxCryptos();
        maxCryptos.add(Crypto.builder().coinId("doge").build());
        maxCryptos.add(Crypto.builder().coinId("shiba").build());
        maxCryptos.add(Crypto.builder().coinId("pepe").build());
        maxCryptos.add(Crypto.builder().coinId("kadena").build());

        when(clock.getZone()).thenReturn(zonedDateTime.getZone());
        when(clock.instant()).thenReturn(zonedDateTime.toInstant());
        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(localDateMinusFiveMinutes, 8)).thenReturn(maxCryptos);
        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(localDateMinusFiveMinutes, 5)).thenReturn(cryptos);
        when(cryptoRepositoryMock.findAllByCoinId("bitcoin")).thenReturn(Optional.of(cryptos));
        when(updateCryptoSchedulerMapperImplMock.mapFrom(cryptos.get(0))).thenReturn(cryptos.get(0));

        updateCryptoPriceScheduler.updateCryptosMarketData();

        verify(updateCryptoSchedulerMapperImplMock, times(1)).mapFrom(cryptos.get(0));
        verify(cryptoRepositoryMock, times(1)).saveAll(cryptos);
    }

    private List<Crypto> getMaxCryptos() {
        List<Crypto> cryptos = new ArrayList<>();
        cryptos.add(Crypto.builder().coinId("bitcoin").build());
        cryptos.add(Crypto.builder().coinId("ethereum").build());
        cryptos.add(Crypto.builder().coinId("binancecoin").build());
        cryptos.add(Crypto.builder().coinId("tether").build());
        cryptos.add(Crypto.builder().coinId("cardano").build());
        cryptos.add(Crypto.builder().coinId("polkadot").build());
        cryptos.add(Crypto.builder().coinId("polygon").build());

        return cryptos;
    }
}