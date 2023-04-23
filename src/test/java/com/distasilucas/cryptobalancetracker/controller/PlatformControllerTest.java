package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;

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
        var platformResponse = new PlatformResponse("Binance");

        when(platformServiceMock.getAllPlatforms()).thenReturn(Collections.singletonList(platformResponse));

        var allPlatforms = platformController.getAllPlatforms();

        assertAll(
                () -> assertNotNull(allPlatforms.getBody()),
                () -> assertEquals(1, allPlatforms.getBody().size())
        );
    }

    @Test
    void shouldRetrievePlatform() {
        var platformResponse = new PlatformResponse("BINANCE");

        when(platformServiceMock.findPlatformByName("binance")).thenReturn(new Platform("BINANCE"));

        var platform = platformController.getPlatform("binance");

        assertAll(
                () -> assertNotNull(platform.getBody()),
                () -> assertEquals(platformResponse, platform.getBody()),
                () -> assertEquals(platformResponse.getName(), platform.getBody().getName())
        );
    }

    @Test
    void shouldAddPlatform() {
        var platformRequest = MockData.getPlatformRequest("LEDGER");
        var platformResponse = MockData.getPlatformResponse("LEDGER");

        when(platformServiceMock.addPlatForm(platformRequest)).thenReturn(platformResponse);

       var responseEntity = platformController.addPlatform(platformRequest);

       assertNotNull(responseEntity.getBody());
       assertAll(
               () -> assertEquals(platformRequest.getName(), responseEntity.getBody().getName()),
               () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode())
       );
    }

    @Test
    void shouldUpdatePlatform() {
        var platformRequest = MockData.getPlatformRequest("LEDGER");
        var platformResponse = MockData.getPlatformResponse("LEDGER");

        when(platformServiceMock.updatePlatform(PLATFORM_NAME, platformRequest)).thenReturn(platformResponse);

        var responseEntity = platformController.updatePlatform(PLATFORM_NAME, platformRequest);

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(platformResponse.getName(), responseEntity.getBody().getName()),
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