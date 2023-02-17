package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.MockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoBalanceResponseMapperImplTest {

    @Mock
    CoingeckoService coingeckoServiceImplMock;

    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoBalanceResponseMapperImpl = new CryptoBalanceResponseMapperImpl(coingeckoServiceImplMock);
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
}