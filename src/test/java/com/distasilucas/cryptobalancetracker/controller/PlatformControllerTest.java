package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformControllerTest {

    @Mock
    PlatformService platformServiceMock;

    PlatformController platformController;

    private static final String PLATFORM_NAME = "Trezor";

    @BeforeEach
    void setUp() {
        platformController = new PlatformController(platformServiceMock);
    }

    @Test
    void shouldRetrieveAllCoinsForPlatform() {
        var platformName = PLATFORM_NAME;
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(platformServiceMock.getAllCoins(platformName)).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = platformController.getCoins(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isPresent())
        );
    }

    @Test
    void shouldReturnNoContentWhenRetrievingAllCoinsForPlatform() {
        var platformName = PLATFORM_NAME;

        when(platformServiceMock.getAllCoins(platformName)).thenReturn(Optional.empty());

        var responseEntity = platformController.getCoins(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isEmpty())
        );
    }

    @Test
    void shouldAddPlatform() {
        var platformDTO = MockData.getPlatformDTO("LEDGER");

        when(platformServiceMock.addPlatForm(platformDTO)).thenReturn(platformDTO);

       var responseEntity = platformController.addPlatform(platformDTO);

       assertNotNull(responseEntity.getBody());
       assertAll(
               () -> assertEquals(platformDTO.getName(), responseEntity.getBody().getName()),
               () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode())
       );
    }

    @Test
    void shouldUpdatePlatform() {
        var platformDTO = MockData.getPlatformDTO("LEDGER");

        when(platformServiceMock.updatePlatform(platformDTO, PLATFORM_NAME)).thenReturn(platformDTO);

        var responseEntity = platformController.updatePlatform(PLATFORM_NAME, platformDTO);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(platformDTO.getName(), responseEntity.getBody().getName()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldUpdatePlatformCoin() {
        var cryptoDTO = MockData.getCryptoDTO();

        when(platformServiceMock.updatePlatformCoin(cryptoDTO, "Safepal", "bitcoin")).thenReturn(cryptoDTO);

        var responseEntity = platformController.updatePlatformCoin(cryptoDTO, "Safepal", "bitcoin");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(cryptoDTO.platform(), responseEntity.getBody().platform()),
                () -> assertEquals(cryptoDTO.coinId(), responseEntity.getBody().coinId())
        );
    }

    @Test
    void shouldDeletePlatform() {
        var responseEntity = platformController.deletePlatform(PLATFORM_NAME);

        assertNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldDeletePlatformCoin() {
        var responseEntity = platformController.deletePlatformCoin(PLATFORM_NAME, "bitcoin");

        assertNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }
}