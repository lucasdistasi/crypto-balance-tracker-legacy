package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.request.crypto.FromPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.ToPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.service.TransferCryptoService;
import com.distasilucas.cryptobalancetracker.service.UserCryptoService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    @Mock
    UserCryptoService userCryptoServiceMocK;

    @Mock
    TransferCryptoService transferCryptoServiceMock;

    CryptoController cryptoController;

    @BeforeEach
    void setUp() {
        cryptoController = new CryptoController(userCryptoServiceMocK, transferCryptoServiceMock);
    }

    @Test
    void shouldGetCryptoWith200StatusCode() {
        var cryptoResponse = UserCryptoResponse.builder()
                .id("bitcoin")
                .build();

        when(userCryptoServiceMocK.getUserCryptoResponse("1234")).thenReturn(cryptoResponse);

        var responseEntity = cryptoController.getCrypto("1234");

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals("bitcoin", responseEntity.getBody().getId())
        );
    }

    @Test
    void shouldRetrieveAllCryptos() {
        var cryptos = MockData.getPageCryptoResponse();
        var page = 0;

        when(userCryptoServiceMocK.getCryptos(page)).thenReturn(Optional.of(cryptos));

        var responseEntity = cryptoController.getCryptos(page);

        assertAll(
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldReturnNoContentIfNoCryptosAreFound() {
        var page = 0;

        when(userCryptoServiceMocK.getCryptos(page)).thenReturn(Optional.empty());

        var responseEntity = cryptoController.getCryptos(page);

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

        when(userCryptoServiceMocK.saveUserCrypto(addCryptoRequest)).thenReturn(cryptoResponse);

        var cryptoResponseEntity = cryptoController.addCrypto(addCryptoRequest);

        assertNotNull(cryptoResponseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.CREATED, cryptoResponseEntity.getStatusCode()),
                () -> assertEquals(addCryptoRequest.getCryptoName(), cryptoResponseEntity.getBody().getCryptoName())
        );
    }

    @Test
    void shouldUpdateCrypto() {
        var newCrypto = new UpdateCryptoRequest("ABC123", BigDecimal.valueOf(0.15), "Binance");
        var newCryptoResponse = UserCryptoResponse.builder()
                .cryptoName("Bitcoin")
                .quantity(BigDecimal.valueOf(0.10))
                .platform("Binance")
                .build();

        when(userCryptoServiceMocK.updateUserCrypto(newCrypto, "ABC123")).thenReturn(newCryptoResponse);

        var responseEntity = cryptoController.updateCrypto(newCrypto, "ABC123");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldDeleteCrypto() {
        doNothing().when(userCryptoServiceMocK).deleteUserCrypto("ABC123");

        var responseEntity = cryptoController.deleteCrypto("ABC123");

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
                "Binance"
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