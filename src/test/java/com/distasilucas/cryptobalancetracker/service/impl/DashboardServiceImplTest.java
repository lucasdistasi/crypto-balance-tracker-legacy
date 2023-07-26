package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import com.sun.source.tree.ModuleTree;
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
    UserCryptoRepository userCryptoRepositoryMock;

    @Mock
    PlatformService platformServiceMock;

    @Mock
    EntityMapper<CryptoBalanceResponse, List<UserCrypto>> cryptoBalanceResponseMapperImplMock;

    @Mock
    BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> coinInfoResponseMapperImplMock;

    @Mock
    UtilValidations utilValidationsMock;

    DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(utilValidationsMock, userCryptoRepositoryMock, platformServiceMock,
                cryptoBalanceResponseMapperImplMock, coinInfoResponseMapperImplMock);
    }

    @Test
    void shouldRetrieveCryptoBalances() {
        var coinInfo = MockData.getBitcoinCoinInfo();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCoin = balanceResponse.coins().get(0);
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);

        when(userCryptoRepositoryMock.findAll()).thenReturn(userCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var coinsBalances = dashboardService.retrieveCryptosBalances();
        var expectedBalance = MockData.getTotalMoney(Collections.singletonList(firstCoin));

        assertAll(
                () -> assertTrue(coinsBalances.isPresent()),
                () -> assertEquals(expectedBalance, coinsBalances.get().totalBalance()),
                () -> assertEquals(BigDecimal.valueOf(5), firstCoin.getQuantity()),
                () -> assertEquals(coinsBalances.get().coins().get(0).getPercentage(), firstCoin.getPercentage()),
                () -> assertEquals(coinInfo, firstCoin.getCoinInfo())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoCryptosAreSaved() {
        when(userCryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var coinsBalances = dashboardService.retrieveCryptosBalances();

        assertAll(
                () -> assertTrue(coinsBalances.isEmpty())
        );
    }

    @Test
    void shouldRetrieveBalancesForCrypto() {
        var coinInfo = MockData.getBitcoinCoinInfo();
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCoin = balanceResponse.coins().get(0);

        when(userCryptoRepositoryMock.findAllByCryptoId("bitcoin")).thenReturn(Optional.of(userCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var coinBalance = dashboardService.retrieveCryptoBalance("bitcoin");
        var expectedBalance = MockData.getTotalMoney(Collections.singletonList(firstCoin));

        assertAll(
                () -> assertTrue(coinBalance.isPresent()),
                () -> assertEquals(expectedBalance, coinBalance.get().totalBalance()),
                () -> assertEquals(BigDecimal.valueOf(5), firstCoin.getQuantity()),
                () -> assertEquals(coinBalance.get().coins().get(0).getPercentage(), firstCoin.getPercentage()),
                () -> assertEquals(coinInfo, firstCoin.getCoinInfo())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoCryptosAreSavedForCryptoBalance() {
        when(userCryptoRepositoryMock.findAllByCryptoId("dogecoin")).thenReturn(Optional.of(Collections.emptyList()));

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
        var coinInfoResponse = MockData.getCoinInfoResponse();
        BiFunction<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> biFunction = (a, b) -> Collections.singletonList(coinInfoResponse);

        when(userCryptoRepositoryMock.findAll()).thenReturn(userCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(cryptoBalanceResponse);
        when(coinInfoResponseMapperImplMock.map()).thenReturn(biFunction);

        var coinsBalanceByPlatform = dashboardService.retrieveCryptosBalanceByPlatform();

        assertAll(
                () -> assertTrue(coinsBalanceByPlatform.isPresent()),
                () -> assertEquals(cryptoBalanceResponse.totalBalance(), coinsBalanceByPlatform.get().totalBalance()),
                () -> assertTrue(CollectionUtils.isNotEmpty(coinsBalanceByPlatform.get().coinInfoResponse())),
                () -> assertEquals(coinInfoResponse.name(), coinsBalanceByPlatform.get().coinInfoResponse().get(0).name())
        );
    }

    @Test
    void shouldRetrieveEmptyCoinsBalanceByPlatform() {
        when(userCryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var coinsBalanceByPlatform = dashboardService.retrieveCryptosBalanceByPlatform();

        assertAll(
                () -> assertTrue(coinsBalanceByPlatform.isEmpty())
        );
    }

    @Test
    void shouldGetPlatformsBalances() {
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(userCryptoRepositoryMock.findAll()).thenReturn(userCryptos);
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

        when(userCryptoRepositoryMock.findAll()).thenReturn(userCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var platformsBalances = dashboardService.getPlatformsBalances();

        assertAll(
                () -> assertTrue(platformsBalances.isEmpty())
        );
    }

    @Test
    void shouldRetrieveAllCoinsForPlatform() {
        var userCrypto = MockData.getUserCrypto();
        var userCryptos = Collections.singletonList(userCrypto);
        var platformEntity = MockData.getPlatform("LEDGER");
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platformEntity);
        when(userCryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.of(userCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(userCryptos)).thenReturn(balanceResponse);

        var allCoins = dashboardService.getAllCoins("LEDGER");

        assertAll(
                () -> assertNotNull(allCoins),
                () -> assertTrue(allCoins.isPresent()),
                () -> assertEquals(balanceResponse.totalBalance(), allCoins.get().totalBalance()),
                () -> assertEquals(1, allCoins.get().coins().size()),
                () -> assertEquals(platformEntity.getName(), allCoins.get().coins().get(0).getPlatform())
        );
    }

    @Test
    void shouldReturnEmptyForGetAllCoins() {
        var platformEntity = MockData.getPlatform("LEDGER");

        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platformEntity);
        when(userCryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.empty());

        var allCoins = dashboardService.getAllCoins("LEDGER");

        assertAll(
                () -> assertTrue(allCoins.isEmpty()),
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

        when(platformServiceMock.getAllPlatforms()).thenReturn(platforms);
        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platform);
        when(userCryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.of(userCryptos));
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
        when(platformServiceMock.getAllPlatforms()).thenReturn(Collections.emptyList());

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

        when(userCryptoRepositoryMock.findAll()).thenReturn(userCryptos);
        when(userCryptoRepositoryMock.findAllByCryptoId(userCrypto.getCryptoId()))
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
        when(userCryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var cryptosPlatformDistribution = dashboardService.getCryptosPlatformDistribution();

        assertAll(
                () -> assertEquals(Optional.empty(), cryptosPlatformDistribution),
                () -> assertTrue(cryptosPlatformDistribution.isEmpty())
        );
    }
}