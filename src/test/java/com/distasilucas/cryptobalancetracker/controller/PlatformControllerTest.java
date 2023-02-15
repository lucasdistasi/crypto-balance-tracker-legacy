package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformControllerTest {

    @Mock
    PlatformService platformServiceMock;

    PlatformController platformController;

    @BeforeEach
    void setUp() {
        platformController = new PlatformController(platformServiceMock);
    }

    @Test
    void shouldRetrieveAllCoinsForPlatform() {
        var platformName = "Trezor";
        var cryptoBalanceResponse = getCryptoBalanceResponse();

        when(platformServiceMock.getAllCoins(platformName)).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = platformController.getCoins(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isPresent())
        );
    }

    @Test
    void shouldReturnNoContentWhenRetrievingAllCoinsForPlatform() {
        var platformName = "Trezor";

        when(platformServiceMock.getAllCoins(platformName)).thenReturn(Optional.empty());

        var responseEntity = platformController.getCoins(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isEmpty())
        );
    }

    @Test
    void shouldAddPlatform() {
        var platformDTO = new PlatformDTO();
        platformDTO.setName("LEDGER");
        var addedPlatform = new PlatformDTO();
        addedPlatform.setName("LEDGER");

        when(platformServiceMock.addPlatForm(platformDTO)).thenReturn(addedPlatform);

       var responseEntity = platformController.addPlatform(platformDTO);

       assertNotNull(responseEntity.getBody());
       assertAll(
               () -> assertEquals(addedPlatform.getName(), responseEntity.getBody().getName()),
               () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode())
       );
    }

    @Test
    void shouldUpdatePlatform() {
        var platformDTO = new PlatformDTO();
        platformDTO.setName("LEDGER");
        var updatedPlatform = new PlatformDTO();
        updatedPlatform.setName("LEDGER");

        when(platformServiceMock.updatePlatform(platformDTO, "Trezor")).thenReturn(updatedPlatform);

        var responseEntity = platformController.updatePlatform("Trezor", platformDTO);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(updatedPlatform.getName(), responseEntity.getBody().getName()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldDeletePlatform() {
        var platformName = "Trezor";

        doNothing().when(platformServiceMock).deletePlatform(platformName);

        var responseEntity = platformController.deletePlatform(platformName);

        assertNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    private CryptoBalanceResponse getCryptoBalanceResponse() {
        var coinInfo = new CoinInfo();
        coinInfo.setSymbol("BTC");

        var coinResponse = new CoinResponse(coinInfo, BigDecimal.valueOf(5), BigDecimal.valueOf(1000), "LEDGER");

        var cryptoBalanceResponse = new CryptoBalanceResponse();
        cryptoBalanceResponse.setTotalBalance(BigDecimal.valueOf(1000));
        cryptoBalanceResponse.setCoins(Collections.singletonList(coinResponse));

        return cryptoBalanceResponse;
    }
}