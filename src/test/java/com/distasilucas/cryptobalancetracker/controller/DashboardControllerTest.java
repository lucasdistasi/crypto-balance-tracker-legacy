package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptosPlatformDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.PlatformsCryptoDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformInfo;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
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
        var coingeckoCryptoInfo = MockData.getBitcoinCoingeckoCryptoInfo();
        var cryptoResponse = MockData.getCryptoResponse(coingeckoCryptoInfo);
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(dashboardServiceMock.retrieveCryptosBalances()).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.retrieveCryptossBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(cryptoResponse.getBalance(), responseEntity.getBody().get().cryptos().get(0).getBalance()),
                () -> assertEquals("btc", responseEntity.getBody().get().cryptos().get(0).getCryptoInfo().getSymbol())
        );
    }

    @Test
    void shouldReturnNoContentForCryptosBalances() {
        when(dashboardServiceMock.retrieveCryptosBalances()).thenReturn(Optional.empty());

        var responseEntity = dashboardController.retrieveCryptossBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveCryptosBalanceByPlatform() {
        var cryptoBalanceResponse = MockData.getCryptoPlatformBalanceResponse();

        when(dashboardServiceMock.retrieveCryptosBalanceByPlatform()).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.retrieveCryptosBalanceByPlatform();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(BigDecimal.valueOf(1000), responseEntity.getBody().get().totalBalance())
        );
    }

    @Test
    void shouldReturnNoContentForCryptosBalanceByPlatform() {
        when(dashboardServiceMock.retrieveCryptosBalanceByPlatform()).thenReturn(Optional.empty());

        var responseEntity = dashboardController.retrieveCryptosBalanceByPlatform();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveCryptosBalances() {
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(dashboardServiceMock.retrieveCryptoBalance("bitcoin")).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.retrieveCryptoBalance("bitcoin");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isPresent()),
                () -> assertEquals(cryptoBalanceResponse.totalBalance(), responseEntity.getBody().get().totalBalance()),
                () -> assertEquals(cryptoBalanceResponse.cryptos().size(), responseEntity.getBody().get().cryptos().size()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveListCryptosPlatformDistribution() {
        var cryptosPlatformDistribution = new CryptosPlatformDistributionResponse("", Collections.emptyList());
        var cryptosPlatforms = Collections.singletonList(cryptosPlatformDistribution);

        when(dashboardServiceMock.getCryptosPlatformDistribution()).thenReturn(Optional.of(cryptosPlatforms));

        var responseEntity = dashboardController.retrieveCryptoBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveNoContentForListCryptosPlatformDistribution() {
        when(dashboardServiceMock.getCryptosPlatformDistribution()).thenReturn(Optional.empty());

        var responseEntity = dashboardController.retrieveCryptoBalance();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldReturnEmptyCryptosBalances() {
        when(dashboardServiceMock.retrieveCryptoBalance("bitcoin")).thenReturn(Optional.empty());

        var responseEntity = dashboardController.retrieveCryptoBalance("bitcoin");

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveAllCryptosForPlatform() {
        var platformName = PLATFORM_NAME;
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        when(dashboardServiceMock.getAllCryptos(platformName)).thenReturn(Optional.of(cryptoBalanceResponse));

        var responseEntity = dashboardController.getCryptos(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isPresent())
        );
    }

    @Test
    void shouldRetrieveListPlatformsCryptoDistributionResponse() {
        var platformsCryptoDistribution = new PlatformsCryptoDistributionResponse("platform", Collections.emptyList());
        var response = Collections.singletonList(platformsCryptoDistribution);

        when(dashboardServiceMock.getPlatformsCryptoDistributionResponse()).thenReturn(Optional.of(response));

        var responseEntity = dashboardController.getPlatformsCryptoDistributionResponse();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldRetrieveNoContentListPlatformsCryptoDistributionResponse() {
        when(dashboardServiceMock.getPlatformsCryptoDistributionResponse()).thenReturn(Optional.empty());

        var responseEntity = dashboardController.getPlatformsCryptoDistributionResponse();

        assertNotNull(responseEntity.getBody());
        assertAll(
                () -> assertTrue(responseEntity.getBody().isEmpty()),
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode())
        );
    }

    @Test
    void shouldGetPlatformsBalances() {
        var platformInfo = new PlatformInfo("Ledger", 50, BigDecimal.valueOf(500));
        var platformBalanceResponse = new PlatformBalanceResponse(BigDecimal.valueOf(1000), Collections.singletonList(platformInfo));

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
    void shouldReturnNoContentWhenRetrievingAllCryptosForPlatform() {
        var platformName = PLATFORM_NAME;

        when(dashboardServiceMock.getAllCryptos(platformName)).thenReturn(Optional.empty());

        var responseEntity = dashboardController.getCryptos(platformName);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().isEmpty())
        );
    }
}