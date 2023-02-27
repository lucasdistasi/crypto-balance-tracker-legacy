package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        var cryptoDTO = MockData.getCryptoDTO();
        var cryptoDTOResponse = MockData.getCryptoDTO();

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
        var coinInfo = MockData.getCoinInfo();
        var coinsResponse = MockData.getCoinResponse(coinInfo);
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(cryptoServiceMocK.retrieveCoinsBalances()).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = cryptoController.retrieveCoinsBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(coinsResponse.getBalance(), responseEntity.getBody().get().getCoins().get(0).getBalance()),
                () -> assertEquals("btc", responseEntity.getBody().get().getCoins().get(0).getCoinInfo().getSymbol())
        );
    }

    @Test
    void shouldRetrieveCoinsBalanceByPlatform() {
        var cryptoBalanceResponse = MockData.getCryptoPlatformBalanceResponse();

        when(cryptoServiceMocK.retrieveCoinsBalanceByPlatform()).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = cryptoController.retrieveCoinsBalanceByPlatform();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(BigDecimal.valueOf(1000), responseEntity.getBody().get().getTotalBalance())
        );
    }

    @Test
    void shouldReturnEmptyCryptosBalances() {
        when(cryptoServiceMocK.retrieveCoinBalance("bitcoin")).thenReturn(Optional.empty());

        var responseEntity = cryptoController.retrieveCoinBalance("bitcoin");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }
}