package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.UserCryptoService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    UtilValidations utilValidationsMock;

    @Mock
    UserCryptoService userCryptoServiceMock;

    @Mock
    PlatformService platformServiceMock;

    @Mock
    EntityMapper<CryptoBalanceResponse, List<UserCrypto>> cryptoBalanceResponseMapperImplMock;

    @Mock
    BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CryptoInfoResponse>> cryptoInfoResponseMapperImplMock;

    DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(utilValidationsMock, userCryptoServiceMock, platformServiceMock,
                cryptoBalanceResponseMapperImplMock, cryptoInfoResponseMapperImplMock);
    }

    @Test
    void shouldRetrieveCryptoBalances() {
        var coingeckoCryptoInfo = MockData.getBitcoinCoingeckoCryptoInfo();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCrypto = balanceResponse.cryptos().get(0);
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);

        when(userCryptoServiceMock.findAll()).thenReturn(userCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var cryptosBalances = dashboardService.retrieveCryptosBalances();
        var expectedBalance = MockData.getTotalMoney(Collections.singletonList(firstCrypto));

        assertAll(
                () -> assertTrue(cryptosBalances.isPresent()),
                () -> assertEquals(expectedBalance, cryptosBalances.get().totalBalance()),
                () -> assertEquals(BigDecimal.valueOf(5), firstCrypto.getQuantity()),
                () -> assertEquals(cryptosBalances.get().cryptos().get(0).getPercentage(), firstCrypto.getPercentage()),
                () -> assertEquals(coingeckoCryptoInfo, firstCrypto.getCryptoInfo())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoCryptosAreSaved() {
        when(userCryptoServiceMock.findAll()).thenReturn(Collections.emptyList());

        var cryptosBalances = dashboardService.retrieveCryptosBalances();

        assertAll(
                () -> assertTrue(cryptosBalances.isEmpty())
        );
    }

    @Test
    void shouldRetrieveBalancesForCrypto() {
        var coingeckoCryptoInfo = MockData.getBitcoinCoingeckoCryptoInfo();
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCrypto = balanceResponse.cryptos().get(0);

        when(userCryptoServiceMock.findAllByCryptoId("bitcoin")).thenReturn(Optional.of(userCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var cryptoBalance = dashboardService.retrieveCryptoBalance("bitcoin");
        var expectedBalance = MockData.getTotalMoney(Collections.singletonList(firstCrypto));

        assertAll(
                () -> assertTrue(cryptoBalance.isPresent()),
                () -> assertEquals(expectedBalance, cryptoBalance.get().totalBalance()),
                () -> assertEquals(BigDecimal.valueOf(5), firstCrypto.getQuantity()),
                () -> assertEquals(cryptoBalance.get().cryptos().get(0).getPercentage(), firstCrypto.getPercentage()),
                () -> assertEquals(coingeckoCryptoInfo, firstCrypto.getCryptoInfo())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoCryptosAreSavedForCryptoBalance() {
        when(userCryptoServiceMock.findAllByCryptoId("dogecoin")).thenReturn(Optional.of(Collections.emptyList()));

        var cryptoBalance = dashboardService.retrieveCryptoBalance("dogecoin");

        assertAll(
                () -> assertTrue(cryptoBalance.isEmpty())
        );
    }

    @Test
    void shouldRetrieveCryptosBalanceByPlatform() {
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();
        var cryptoInfoResponse = MockData.getCryptoInfoResponse();
        BiFunction<Map<String, BigDecimal>, CryptoBalanceResponse, List<CryptoInfoResponse>> biFunction = (a, b) -> Collections.singletonList(cryptoInfoResponse);

        when(userCryptoServiceMock.findAll()).thenReturn(userCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(cryptoBalanceResponse);
        when(cryptoInfoResponseMapperImplMock.map()).thenReturn(biFunction);

        var cryptosBalanceByPlatform = dashboardService.retrieveCryptosBalanceByPlatform();

        assertAll(
                () -> assertTrue(cryptosBalanceByPlatform.isPresent()),
                () -> assertEquals(cryptoBalanceResponse.totalBalance(), cryptosBalanceByPlatform.get().totalBalance()),
                () -> assertTrue(CollectionUtils.isNotEmpty(cryptosBalanceByPlatform.get().cryptoInfoResponse())),
                () -> assertEquals(cryptoInfoResponse.name(), cryptosBalanceByPlatform.get().cryptoInfoResponse().get(0).name())
        );
    }

    @Test
    void shouldRetrieveEmptyCryptosBalanceByPlatform() {
        when(userCryptoServiceMock.findAll()).thenReturn(Collections.emptyList());

        var cryptosBalanceByPlatform = dashboardService.retrieveCryptosBalanceByPlatform();

        assertAll(
                () -> assertTrue(cryptosBalanceByPlatform.isEmpty())
        );
    }

    @Test
    void shouldGetPlatformsBalances() {
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(userCryptoServiceMock.findAll()).thenReturn(userCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var platformsBalances = dashboardService.getPlatformsBalances();

        assertAll(
                () -> assertTrue(platformsBalances.isPresent()),
                () -> assertEquals(BigDecimal.valueOf(1000), platformsBalances.get().totalBalance()),
                () -> assertEquals(1, platformsBalances.get().platforms().size())
        );
    }

    @Test
    void shouldReturnEmptyGetPlatformsBalances() {
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var balanceResponse = new CryptoBalanceResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, Collections.emptyList());

        when(userCryptoServiceMock.findAll()).thenReturn(userCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var platformsBalances = dashboardService.getPlatformsBalances();

        assertAll(
                () -> assertTrue(platformsBalances.isEmpty())
        );
    }

    @Test
    void shouldRetrieveAllCryptosForPlatform() {
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var platformEntity = MockData.getPlatform("LEDGER");
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platformEntity);
        when(userCryptoServiceMock.findAllByPlatformId("1234")).thenReturn(Optional.of(userCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var allCryptos = dashboardService.getAllCryptos("LEDGER");

        assertAll(
                () -> assertNotNull(allCryptos),
                () -> assertTrue(allCryptos.isPresent()),
                () -> assertEquals(balanceResponse.totalBalance(), allCryptos.get().totalBalance()),
                () -> assertEquals(1, allCryptos.get().cryptos().size()),
                () -> assertEquals(platformEntity.getName(), allCryptos.get().cryptos().get(0).getPlatform())
        );
    }

    @Test
    void shouldReturnEmptyForGetAllCryptos() {
        var platformEntity = MockData.getPlatform("LEDGER");

        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platformEntity);
        when(userCryptoServiceMock.findAllByPlatformId("1234")).thenReturn(Optional.empty());

        var allCryptos = dashboardService.getAllCryptos("LEDGER");

        assertAll(
                () -> assertTrue(allCryptos.isEmpty()),
                () -> verify(cryptoBalanceResponseMapperImplMock, never()).mapFrom(any())
        );
    }

    @Test
    void shouldReturnListOfPlatformsCryptoDistributionResponse() {
        var platforms = Collections.singletonList(new PlatformResponse("LEDGER"));
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var platform = MockData.getPlatform("LEDGER");

        when(platformServiceMock.getAllPlatformsResponse()).thenReturn(platforms);
        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platform);
        when(userCryptoServiceMock.findAllByPlatformId("1234")).thenReturn(Optional.of(userCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var platformsCryptoDistributionResponse = dashboardService.getPlatformsCryptoDistributionResponse();

        assertTrue(platformsCryptoDistributionResponse.isPresent());
        assertAll(
                () -> assertFalse(platformsCryptoDistributionResponse.get().isEmpty()),
                () -> assertEquals(platforms.size(), platformsCryptoDistributionResponse.get().size())
        );
    }

    @Test
    void shouldReturnEmptyListOfPlatformsCryptoDistributionResponse() {
        when(platformServiceMock.getAllPlatformsResponse()).thenReturn(Collections.emptyList());

        var platformsCryptoDistributionResponse = dashboardService.getPlatformsCryptoDistributionResponse();

        assertAll(
                () -> assertEquals(Optional.empty(), platformsCryptoDistributionResponse),
                () -> assertTrue(platformsCryptoDistributionResponse.isEmpty())
        );
    }

    @Test
    void shouldReturnListOfCryptosPlatformDistributionResponse() {
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(userCryptoServiceMock.findAll()).thenReturn(userCryptos);
        when(userCryptoServiceMock.findAllByCryptoId(userCrypto.getCryptoId()))
                .thenReturn(Optional.of(userCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var cryptosPlatformDistribution = dashboardService.getCryptosPlatformDistribution();

        assertTrue(cryptosPlatformDistribution.isPresent());
        assertAll(
                () -> assertFalse(cryptosPlatformDistribution.get().isEmpty()),
                () -> assertEquals(userCryptos.size(), cryptosPlatformDistribution.get().size())
        );
    }

    @Test
    void shouldReturnEmptyListOfCryptosPlatformDistributionResponse() {
        when(userCryptoServiceMock.findAll()).thenReturn(Collections.emptyList());

        var cryptosPlatformDistribution = dashboardService.getCryptosPlatformDistribution();

        assertAll(
                () -> assertEquals(Optional.empty(), cryptosPlatformDistribution),
                () -> assertTrue(cryptosPlatformDistribution.isEmpty())
        );
    }
}