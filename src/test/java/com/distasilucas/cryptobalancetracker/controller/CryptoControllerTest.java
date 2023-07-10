package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.request.crypto.FromPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.ToPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.TransferCryptoService;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    @Mock
    CryptoService cryptoServiceMocK;

    @Mock
    TransferCryptoService transferCryptoServiceMock;

    CryptoController cryptoController;

    @BeforeEach
    void setUp() {
        cryptoController = new CryptoController(cryptoServiceMocK, transferCryptoServiceMock);
    }

    @Test
    void shouldGetCoinWith200StatusCode() {
        var cryptoResponse = CryptoResponse.builder()
                .coinId("bitcoin")
                .build();

        when(cryptoServiceMocK.getCoin("1234")).thenReturn(cryptoResponse);

        var responseEntity = cryptoController.getCoin("1234");

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals("bitcoin", responseEntity.getBody().getCoinId())
        );
    }

    @Test
    void shouldRetrieveAllCoins() {
        var cryptos = MockData.getPageCryptoResponse();
        var page = 0;

        when(cryptoServiceMocK.getCoins(page)).thenReturn(Optional.of(cryptos));

        var responseEntity = cryptoController.getCoins(page);

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldReturnNoContentIfNoCoinsAreFound() {
        var page = 0;

        when(cryptoServiceMocK.getCoins(page)).thenReturn(Optional.empty());

        var responseEntity = cryptoController.getCoins(page);

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldReturnCreatedCrypto() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var cryptoResponse = MockData.getCryptoResponse();

        when(cryptoServiceMocK.addCoin(addCryptoRequest)).thenReturn(cryptoResponse);

        var cryptoResponseEntity = cryptoController.addCoin(addCryptoRequest);

        assertNotNull(cryptoResponseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.CREATED, cryptoResponseEntity.getStatusCode()),
                () -> assertEquals(addCryptoRequest.getCoinName(), cryptoResponseEntity.getBody().getCoinName())
        );
    }

    @Test
    void shouldUpdateCoin() {
        var newCrypto = new UpdateCryptoRequest("ABC123", BigDecimal.valueOf(0.15), "Binance");
        var newCryptoResponse = CryptoResponse.builder()
                .coinName("Bitcoin")
                .quantity(BigDecimal.valueOf(0.10))
                .platform("Binance")
                .build();

        when(cryptoServiceMocK.updateCoin(newCrypto, "ABC123")).thenReturn(newCryptoResponse);

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

    @Test
    void shouldTransferCrypto() {
        var fromPlatform = new FromPlatform();
        var toPlatform = new ToPlatform();
        var transferCryptoResponse = new TransferCryptoResponse(fromPlatform, toPlatform);

        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(0.2),
                BigDecimal.valueOf(0.001),
                "Binance",
                "Safepal"
        );

        when(transferCryptoServiceMock.transferCrypto(transferCryptoRequest))
                .thenReturn(transferCryptoResponse);

        var responseEntity = cryptoController.transferCrypto(transferCryptoRequest);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }
}