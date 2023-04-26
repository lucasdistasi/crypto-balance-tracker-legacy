package com.distasilucas.cryptobalancetracker.scheduler;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCryptoPriceSchedulerTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    @Mock
    CoingeckoService coingeckoServiceMock;

    UpdateCryptoPriceScheduler updateCryptoPriceScheduler;

    @BeforeEach
    void setUp() {
        updateCryptoPriceScheduler = new UpdateCryptoPriceScheduler(cryptoRepositoryMock, platformRepositoryMock, coingeckoServiceMock);
    }

    @Test
    void shouldExecuteSchedulerWithDifferentPrices() {
        var cryptos = MockData.getAllCryptos();
        var coinInfo = MockData.getCoinInfo();

        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(5)).thenReturn(cryptos);
        when(coingeckoServiceMock.retrieveCoinInfo("bitcoin")).thenReturn(coinInfo);

        updateCryptoPriceScheduler.updateCryptoLastKnownPrice();

        verify(cryptoRepositoryMock, times(1)).save(cryptos.get(0));
    }

    @Test
    void shouldExecuteSchedulerWithSamePrices() {
        var crypto = MockData.getCrypto("123");
        crypto.setLastKnownPrice(BigDecimal.valueOf(21750));
        var cryptos = Collections.singletonList(crypto);
        var coinInfo = MockData.getCoinInfo();
        var currentPrice = new CurrentPrice(BigDecimal.valueOf(21750), BigDecimal.valueOf(23000), BigDecimal.valueOf(1));
        var marketDate = new MarketData(currentPrice, BigDecimal.valueOf(18000000), new BigDecimal(21000000));
        coinInfo.setMarketData(marketDate);

        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(5)).thenReturn(cryptos);
        when(coingeckoServiceMock.retrieveCoinInfo("bitcoin")).thenReturn(coinInfo);

        updateCryptoPriceScheduler.updateCryptoLastKnownPrice();

        verify(cryptoRepositoryMock, times(1)).save(cryptos.get(0));
    }

    @Test
    void shouldNotUpdateIfWebClientResponseExceptionIsThrown() {
        var cryptos = MockData.getAllCryptos();
        var webClientResponseException = new WebClientResponseException(HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", null, null, null);

        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(5)).thenReturn(cryptos);
        doThrow(webClientResponseException).when(coingeckoServiceMock).retrieveCoinInfo("bitcoin");

        updateCryptoPriceScheduler.updateCryptoLastKnownPrice();

        verify(cryptoRepositoryMock, never()).save(cryptos.get(0));
    }

    @Test
    void shouldNotUpdateIfNonCaughtExceptionIsThrown() {
        var cryptos = MockData.getAllCryptos();
        var runtimeException = new RuntimeException("RuntimeException");

        when(cryptoRepositoryMock.findTopNCryptosOrderByLastPriceUpdatedAtAsc(5)).thenReturn(cryptos);
        doThrow(runtimeException).when(coingeckoServiceMock).retrieveCoinInfo("bitcoin");

        updateCryptoPriceScheduler.updateCryptoLastKnownPrice();

        verify(cryptoRepositoryMock, never()).save(cryptos.get(0));
    }

}