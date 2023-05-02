package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformService platformServiceMock;

    @Mock
    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImplMock;

    @Mock
    BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> coinInfoResponseMapperImplMock;

    DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(cryptoRepositoryMock, platformServiceMock,
                cryptoBalanceResponseMapperImplMock, coinInfoResponseMapperImplMock);
    }

    @Test
    void shouldRetrieveCryptoBalances() {
        var coinInfo = MockData.getCoinInfo();
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCoin = balanceResponse.coins().get(0);

        when(cryptoRepositoryMock.findAll()).thenReturn(cryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var coinsBalances = dashboardService.retrieveCoinsBalances();
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
        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var coinsBalances = dashboardService.retrieveCoinsBalances();

        assertAll(
                () -> assertTrue(coinsBalances.isEmpty())
        );
    }

    @Test
    void shouldRetrieveBalancesForCrypto() {
        var coinInfo = MockData.getCoinInfo();
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCoin = balanceResponse.coins().get(0);

        when(cryptoRepositoryMock.findAllByCoinId("bitcoin")).thenReturn(Optional.of(cryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var coinBalance = dashboardService.retrieveCoinBalance("bitcoin");
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
        when(cryptoRepositoryMock.findAllByCoinId("dogecoin")).thenReturn(Optional.of(Collections.emptyList()));

        var coinBalance = dashboardService.retrieveCoinBalance("dogecoin");

        assertAll(
                () -> assertTrue(coinBalance.isEmpty())
        );
    }

    @Test
    void shouldRetrieveCoinsBalanceByPlatform() {
        var allCryptos = MockData.getAllCryptos();
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();
        var coinInfoResponse = MockData.getCoinInfoResponse();
        BiFunction<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> biFunction = (a, b) -> Collections.singletonList(coinInfoResponse);

        when(cryptoRepositoryMock.findAll()).thenReturn(allCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(allCryptos)).thenReturn(cryptoBalanceResponse);
        when(coinInfoResponseMapperImplMock.map()).thenReturn(biFunction);

        var coinsBalanceByPlatform = dashboardService.retrieveCoinsBalanceByPlatform();

        assertAll(
                () -> assertTrue(coinsBalanceByPlatform.isPresent()),
                () -> assertEquals(cryptoBalanceResponse.totalBalance(), coinsBalanceByPlatform.get().totalBalance()),
                () -> assertTrue(CollectionUtils.isNotEmpty(coinsBalanceByPlatform.get().coinInfoResponse())),
                () -> assertEquals(coinInfoResponse.name(), coinsBalanceByPlatform.get().coinInfoResponse().get(0).name())
        );
    }

    @Test
    void shouldRetrieveEmptyCoinsBalanceByPlatform() {
        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var coinsBalanceByPlatform = dashboardService.retrieveCoinsBalanceByPlatform();

        assertAll(
                () -> assertTrue(coinsBalanceByPlatform.isEmpty())
        );
    }

    @Test
    void shouldGetPlatformsBalances() {
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(cryptoRepositoryMock.findAll()).thenReturn(cryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var platformsBalances = dashboardService.getPlatformsBalances();

        assertAll(
                () -> assertTrue(platformsBalances.isPresent()),
                () -> assertEquals(BigDecimal.valueOf(1000), platformsBalances.get().totalBalance()),
                () -> assertEquals(1, platformsBalances.get().platforms().size())
        );
    }

    @Test
    void shouldReturnEmptyGetPlatformsBalances() {
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = new CryptoBalanceResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, Collections.emptyList());

        when(cryptoRepositoryMock.findAll()).thenReturn(cryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var platformsBalances = dashboardService.getPlatformsBalances();

        assertAll(
                () -> assertTrue(platformsBalances.isEmpty())
        );
    }

    @Test
    void shouldRetrieveAllCoinsForPlatform() {
        var platformEntity = MockData.getPlatform("LEDGER");
        var allCryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platformEntity);
        when(cryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.of(allCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(allCryptos)).thenReturn(balanceResponse);

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
        when(cryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.empty());

        var allCoins = dashboardService.getAllCoins("LEDGER");

        assertAll(
                () -> assertTrue(allCoins.isEmpty()),
                () -> verify(cryptoBalanceResponseMapperImplMock, never()).mapFrom(any())
        );
    }

    @Test
    void shouldReturnListOfPlatformsCryptoDistributionResponse() {
        var platforms = Collections.singletonList(new PlatformResponse("LEDGER"));
        var allCryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var platform = MockData.getPlatform("LEDGER");

        when(platformServiceMock.getAllPlatforms()).thenReturn(platforms);
        when(platformServiceMock.findPlatformByName("LEDGER")).thenReturn(platform);
        when(cryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.of(allCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(allCryptos)).thenReturn(balanceResponse);

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
        var allCryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(cryptoRepositoryMock.findAll()).thenReturn(allCryptos);
        when(cryptoRepositoryMock.findAllByCoinId("bitcoin")).thenReturn(Optional.of(allCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(allCryptos)).thenReturn(balanceResponse);

        var cryptosPlatformDistribution = dashboardService.getCryptosPlatformDistribution();

        assertTrue(cryptosPlatformDistribution.isPresent());
        assertAll(
                () -> assertFalse(cryptosPlatformDistribution.get().isEmpty()),
                () -> assertEquals(allCryptos.size(), cryptosPlatformDistribution.get().size())
        );
    }

    @Test
    void shouldReturnEmptyListOfCryptosPlatformDistributionResponse() {
        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var cryptosPlatformDistribution = dashboardService.getCryptosPlatformDistribution();

        assertAll(
                () -> assertEquals(Optional.empty(), cryptosPlatformDistribution),
                () -> assertTrue(cryptosPlatformDistribution.isEmpty())
        );
    }
}