package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinsResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    @Mock
    CryptoService<Crypto, CryptoDTO> cryptoServiceMocK;

    CryptoController cryptoController;

    @BeforeEach
    void setUp() {
        cryptoController = new CryptoController(cryptoServiceMocK);
    }

    @Test
    void shouldReturnCreatedCrypto() {
        var cryptoDTO = CryptoDTO.builder()
                .name("btc")
                .build();
        var crypto = Crypto.builder()
                .ticker("btc")
                .build();

        when(cryptoServiceMocK.addCrypto(cryptoDTO)).thenReturn(crypto);

        var cryptoResponseEntity = cryptoController.addCrypto(cryptoDTO);

        assertNotNull(cryptoResponseEntity.getBody());
        assertAll("cryptoResponseEntity",
                () -> assertEquals(HttpStatus.CREATED, cryptoResponseEntity.getStatusCode()),
                () -> assertEquals(cryptoDTO.getName(), cryptoResponseEntity.getBody().getTicker())
        );
    }

    @Test
    void shouldReturnCryptosBalances() {
        var currentPrice = new CurrentPrice();
        currentPrice.setUsd(BigDecimal.valueOf(100));

        var marketData = new MarketData();
        marketData.setCurrentPrice(currentPrice);

        var coinInfo = new CoinInfo();
        coinInfo.setId("coinId");
        coinInfo.setName("coinName");
        coinInfo.setSymbol("btc");
        coinInfo.setMarketData(marketData);

        var coinsResponse = new CoinsResponse(coinInfo, BigDecimal.valueOf(2), BigDecimal.valueOf(10));
        var cryptoCoinsResponse = new CryptoBalanceResponse(BigDecimal.valueOf(150), Collections.singletonList(coinsResponse));

        when(cryptoServiceMocK.retrieveCoinsBalances()).thenReturn(cryptoCoinsResponse);

        var responseEntity = cryptoController.retrieveCoinsBalance();

        assertNotNull(responseEntity.getBody());
        assertAll("cryptoResponseEntity",
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(coinsResponse.getBalance(), responseEntity.getBody().getCoins().get(0).getBalance()),
                () -> assertEquals("btc", responseEntity.getBody().getCoins().get(0).getCoinInfo().getSymbol())
        );
    }
}