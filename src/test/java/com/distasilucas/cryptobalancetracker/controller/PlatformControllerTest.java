package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
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
    void shouldRetrieveAllPlatforms() {
        when(platformServiceMock.getAllPlatforms()).thenReturn(Collections.singletonList(new PlatformDTO("Binance")));

        var allPlatforms = platformController.getAllPlatforms();

        assertAll(
                () -> assertNotNull(allPlatforms.getBody()),
                () -> assertEquals(1, allPlatforms.getBody().size())
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
    void shouldDeletePlatform() {
        var responseEntity = platformController.deletePlatform(PLATFORM_NAME);

        assertNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }
}