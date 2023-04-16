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
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void shouldGetCoinWith200StatusCode() {
        var crypto = CryptoDTO.builder()
                .coinId("bitcoin")
                .build();

        when(cryptoServiceMocK.getCoin("1234")).thenReturn(Optional.of(crypto));

        var responseEntity = cryptoController.getCoin("1234");

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals("bitcoin", responseEntity.getBody().get().coinId())
        );
    }

    @Test
    void shouldReturn404WhenRetrievingNonExistingCoin() {
        when(cryptoServiceMocK.getCoin("1234")).thenReturn(Optional.empty());

        var responseEntity = cryptoController.getCoin("1234");

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveAllCoins() {
        var cryptos = Collections.singletonList(MockData.getCryptoDTO());

        when(cryptoServiceMocK.getCoins()).thenReturn(Optional.of(cryptos));

        var responseEntity = cryptoController.getCoins();

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(1, responseEntity.getBody().get().size())
        );
    }

    @Test
    void shouldReturnNoContentIfNoCoinsAreFound() {
        when(cryptoServiceMocK.getCoins()).thenReturn(Optional.empty());

        var responseEntity = cryptoController.getCoins();

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
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