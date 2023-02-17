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

    private static final String DOGECOIN = "Dogecoin";
    private static final String BITCOIN = "Bitcoin";

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

        when(cryptoServiceMocK.retrieveCoinsBalances()).thenReturn(cryptoBalanceResponse);

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
        var cryptoDTO = MockData.getCryptoDTO();
        var cryptoDTOResponse = MockData.getCryptoDTO();

        when(cryptoServiceMocK.updateCoin(cryptoDTO, BITCOIN)).thenReturn(cryptoDTOResponse);

        var responseEntity = cryptoController.updateCrypto(cryptoDTO, BITCOIN);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(cryptoDTOResponse.quantity(), responseEntity.getBody().quantity())
        );
    }

    @Test
    void shouldDeleteCrypto() {
        doNothing().when(cryptoServiceMocK).deleteCoin(DOGECOIN);

        var responseEntity = cryptoController.deleteCoin(DOGECOIN);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
}