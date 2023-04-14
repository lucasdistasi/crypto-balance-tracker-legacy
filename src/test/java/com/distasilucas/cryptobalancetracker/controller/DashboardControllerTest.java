package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.response.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    DashboardService dashboardServiceMock;

    DashboardController dashboardController;

    private static final String PLATFORM_NAME = "Trezor";

    @BeforeEach
    void setUp() {
        dashboardController = new DashboardController(dashboardServiceMock);
    }

    @Test
    void shouldReturnCryptosBalances() {
        var coinInfo = MockData.getCoinInfo();
        var coinsResponse = MockData.getCoinResponse(coinInfo);
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(dashboardServiceMock.retrieveCoinsBalances()).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.retrieveCoinsBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(coinsResponse.getBalance(), responseEntity.getBody().get().getCoins().get(0).getBalance()),
                () -> assertEquals("btc", responseEntity.getBody().get().getCoins().get(0).getCoinInfo().getSymbol())
        );
    }

    @Test
    void shouldReturnNoContentForCryptosBalances() {
        when(dashboardServiceMock.retrieveCoinsBalances()).thenReturn(Optional.empty());

        var responseEntity = dashboardController.retrieveCoinsBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveCoinsBalanceByPlatform() {
        var cryptoBalanceResponse = MockData.getCryptoPlatformBalanceResponse();

        when(dashboardServiceMock.retrieveCoinsBalanceByPlatform()).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.retrieveCoinsBalanceByPlatform();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(BigDecimal.valueOf(1000), responseEntity.getBody().get().getTotalBalance())
        );
    }

    @Test
    void shouldReturnNoContentForCoinsBalanceByPlatform() {
        when(dashboardServiceMock.retrieveCoinsBalanceByPlatform()).thenReturn(Optional.empty());

        var responseEntity = dashboardController.retrieveCoinsBalanceByPlatform();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveCryptosBalances() {
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(dashboardServiceMock.retrieveCoinBalance("bitcoin")).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.retrieveCoinBalance("bitcoin");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(cryptoBalanceResponse.getTotalBalance(), responseEntity.getBody().get().getTotalBalance()),
                () -> assertEquals(cryptoBalanceResponse.getCoins().size(), responseEntity.getBody().get().getCoins().size()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldReturnEmptyCryptosBalances() {
        when(dashboardServiceMock.retrieveCoinBalance("bitcoin")).thenReturn(Optional.empty());

        var responseEntity = dashboardController.retrieveCoinBalance("bitcoin");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveAllCoinsForPlatform() {
        var platformName = PLATFORM_NAME;
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(dashboardServiceMock.getAllCoins(platformName)).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.getCoins(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isPresent())
        );
    }

    @Test
    void shouldGetPlatformsBalances() {
        var platformBalanceResponse = PlatformBalanceResponse.builder()
                .totalBalance(BigDecimal.valueOf(1000))
                .build();

        when(dashboardServiceMock.getPlatformsBalances()).thenReturn(Optional.of(platformBalanceResponse));

        var responseEntity = dashboardController.getPlatformsBalances();

        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isPresent())
        );
    }

    @Test
    void shouldReturnNoContentWhenGetPlatformsBalances() {
        when(dashboardServiceMock.getPlatformsBalances()).thenReturn(Optional.empty());

        var responseEntity = dashboardController.getPlatformsBalances();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldReturnNoContentWhenRetrievingAllCoinsForPlatform() {
        var platformName = PLATFORM_NAME;

        when(dashboardServiceMock.getAllCoins(platformName)).thenReturn(Optional.empty());

        var responseEntity = dashboardController.getCoins(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isEmpty())
        );
    }
}