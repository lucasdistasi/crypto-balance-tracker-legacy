package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    @Mock
    CryptoService<CryptoDTO> cryptoServiceMocK;

    CryptoController cryptoController;

    @BeforeEach
    void setUp() {
        cryptoController = new CryptoController(cryptoServiceMocK);
    }

    @Test
    void shouldReturnCreatedCrypto() {
        var cryptoDTO = CryptoDTO.builder()
                .coin_name("Bitcoin")
                .build();

        var cryptoDTOResponse = CryptoDTO.builder()
                .coin_name("Bitcoin")
                .ticker("btc")
                .build();

        when(cryptoServiceMocK.addCoin(cryptoDTO)).thenReturn(cryptoDTOResponse);

        var cryptoResponseEntity = cryptoController.addCoin(cryptoDTO);

        assertNotNull(cryptoResponseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.CREATED, cryptoResponseEntity.getStatusCode()),
                () -> assertEquals(cryptoDTO.coin_name(), cryptoResponseEntity.getBody().coin_name())
        );
    }

    @Test
    void shouldReturnCryptosBalances() {
        var currentPrice = new CurrentPrice(BigDecimal.valueOf(100));
        var marketData = new MarketData(currentPrice);
        var coinInfo = new CoinInfo();
        coinInfo.setId("coinId");
        coinInfo.setName("coin_name");
        coinInfo.setSymbol("btc");
        coinInfo.setMarketData(marketData);

        var coinsResponse = new CoinResponse(coinInfo, BigDecimal.valueOf(2), BigDecimal.valueOf(10), "Binance");
        var cryptoCoinsResponse = new CryptoBalanceResponse(BigDecimal.valueOf(150), Collections.singletonList(coinsResponse));

        when(cryptoServiceMocK.retrieveCoinsBalances()).thenReturn(cryptoCoinsResponse);

        var responseEntity = cryptoController.retrieveCoinsBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(coinsResponse.getBalance(), responseEntity.getBody().getCoins().get(0).getBalance()),
                () -> assertEquals("btc", responseEntity.getBody().getCoins().get(0).getCoinInfo().getSymbol())
        );
    }

    @Test
    void shouldUpdateCrypto() {
        var cryptoDTO = CryptoDTO.builder()
                .coin_name("btc")
                .quantity(BigDecimal.valueOf(2))
                .build();
        var cryptoDTOResponse = CryptoDTO.builder()
                .coin_name("btc")
                .quantity(BigDecimal.valueOf(1))
                .build();

        when(cryptoServiceMocK.updateCoin(cryptoDTO, "Bitcoin")).thenReturn(cryptoDTOResponse);

        var responseEntity = cryptoController.updateCrypto(cryptoDTO, "Bitcoin");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(cryptoDTOResponse.quantity(), responseEntity.getBody().quantity())
        );
    }

    @Test
    void shouldDeleteCrypto() {
        doNothing().when(cryptoServiceMocK).deleteCoin("Dogecoin");

        var responseEntity = cryptoController.deleteCoin("Dogecoin");

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
}