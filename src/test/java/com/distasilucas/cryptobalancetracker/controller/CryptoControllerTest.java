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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    @Mock
    CryptoService cryptoServiceMocK;

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
    void shouldUpdateCoin() {
        var originalCrypto = new CryptoDTO("Bitcoin", BigDecimal.valueOf(0.10), "Binance", "BTC", "bitcoin", BigDecimal.valueOf(30000));
        var newCrypto = new CryptoDTO("Bitcoin", BigDecimal.valueOf(0.15), "Binance", "BTC", "bitcoin", BigDecimal.valueOf(30000));

        when(cryptoServiceMocK.updateCoin(newCrypto, "ABC123")).thenReturn(originalCrypto);

        var responseEntity = cryptoController.updateCoin(newCrypto, "ABC123");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldDeleteCoin() {
        doNothing().when(cryptoServiceMocK).deleteCoin("ABC123");

        var responseEntity = cryptoController.deleteCoin("ABC123");

        assertNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }
}