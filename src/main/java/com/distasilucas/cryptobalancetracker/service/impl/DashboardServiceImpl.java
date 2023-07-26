package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptosPlatformDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.PlatformsCryptoDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformInfo;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UtilValidations utilValidations;
    private final UserCryptoRepository userCryptoRepository;
    private final PlatformService platformService;
    private final EntityMapper<CryptoBalanceResponse, List<UserCrypto>> cryptoBalanceResponseMapperImpl;
    private final BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> coinInfoResponseMapperImpl;

    @Override
    public Optional<CryptoBalanceResponse> retrieveCryptosBalances() {
        log.info("Retrieving cryptos balances");
        List<UserCrypto> allCoins = userCryptoRepository.findAll();

        return CollectionUtils.isEmpty(allCoins) ?
                Optional.empty() :
                Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(allCoins));
    }

    @Override
    public Optional<CryptoBalanceResponse> retrieveCryptoBalance(String cryptoId) {
        log.info("Retrieving balances for coin [{}]", cryptoId);
        Optional<List<UserCrypto>> allCoins = userCryptoRepository.findAllByCryptoId(cryptoId);

        return allCoins.isEmpty() || CollectionUtils.isEmpty(allCoins.get()) ?
                Optional.empty() :
                Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(allCoins.get()));
    }

    @Override
    public Optional<CryptoPlatformBalanceResponse> retrieveCryptosBalanceByPlatform() {
        log.info("Retrieving cryptos by platform");
        List<UserCrypto> allCoins = userCryptoRepository.findAll();

        if (CollectionUtils.isNotEmpty(allCoins)) {
            CryptoBalanceResponse cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(allCoins);
            Map<String, BigDecimal> coinByPlatform = new HashMap<>();

            cryptoBalanceResponse.coins()
                    .forEach(coin -> {
                        String coinName = coin.getCoinInfo().getName();
                        BigDecimal balance = coin.getBalance();

                        coinByPlatform.compute(coinName, (k, v) -> (v == null) ? balance : v.add(balance));
                    });

            List<CoinInfoResponse> coinInfoResponses = coinInfoResponseMapperImpl.map()
                    .apply(coinByPlatform, cryptoBalanceResponse);

            CryptoPlatformBalanceResponse cryptoPlatformBalanceResponse = new CryptoPlatformBalanceResponse(
                    cryptoBalanceResponse.totalBalance(), coinInfoResponses
            );

            return Optional.of(cryptoPlatformBalanceResponse);
        }

        return Optional.empty();
    }

    @Override
    public Optional<PlatformBalanceResponse> getPlatformsBalances() {
        List<UserCrypto> cryptos = userCryptoRepository.findAll();
        CryptoBalanceResponse cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(cryptos);
        List<CoinResponse> coins = cryptoBalanceResponse.coins();

        if (CollectionUtils.isNotEmpty(coins)) {
            Map<String, BigDecimal> balancePerPlatform = new HashMap<>();

            coins.forEach(coin -> {
                String platform = coin.getPlatform();
                BigDecimal balance = coin.getBalance();

                balancePerPlatform.compute(platform, (k, v) -> (v == null) ? balance : v.add(balance));
            });

            BigDecimal totalBalance = cryptoBalanceResponse.totalBalance();
            List<PlatformInfo> platforms = getPlatformInfo(balancePerPlatform, totalBalance);
            PlatformBalanceResponse platformBalanceResponse = new PlatformBalanceResponse(totalBalance, platforms);
            return Optional.of(platformBalanceResponse);
        }

        return Optional.empty();
    }

    @Override
    public Optional<CryptoBalanceResponse> getAllCoins(String platformName) {
        utilValidations.validatePlatformNameFormat(platformName);
        log.info("Retrieving coins in platform {}", platformName);
        Platform platform = platformService.findPlatformByName(platformName);
        Optional<List<UserCrypto>> cryptos = userCryptoRepository.findAllByPlatformId(platform.getId());

        Optional<CryptoBalanceResponse> cryptoBalanceResponse = Optional.empty();

        if (cryptos.isPresent() && CollectionUtils.isNotEmpty(cryptos.get())) {
            cryptoBalanceResponse = Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(cryptos.get()));
        }

        return cryptoBalanceResponse;
    }

    @Override
    public Optional<List<PlatformsCryptoDistributionResponse>> getPlatformsCryptoDistributionResponse() {
        List<String> platformNames = platformService.getAllPlatforms()
                .stream()
                .map(PlatformResponse::getName)
                .toList();
        List<PlatformsCryptoDistributionResponse> platformsCryptoDistributionResponse = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(platformNames)) {
            platformNames.forEach(platform -> {
                Optional<CryptoBalanceResponse> cryptoBalanceResponse = getAllCoins(platform);
                cryptoBalanceResponse.ifPresent(response -> platformsCryptoDistributionResponse.add(new PlatformsCryptoDistributionResponse(platform, response.coins())));
            });
        }

        return CollectionUtils.isNotEmpty(platformsCryptoDistributionResponse) ?
                Optional.of(platformsCryptoDistributionResponse) :
                Optional.empty();
    }

    @Override
    public Optional<List<CryptosPlatformDistributionResponse>> getCryptosPlatformDistribution() {
        Set<String> cryptosIds = userCryptoRepository.findAll()
                .stream()
                .map(UserCrypto::getCryptoId)
                .collect(Collectors.toSet());
        List<CryptosPlatformDistributionResponse> cryptosPlatformDistribution = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(cryptosIds)) {
            cryptosIds.forEach(coinId -> {
                Optional<CryptoBalanceResponse> cryptoBalanceResponse = retrieveCryptoBalance(coinId);

                cryptoBalanceResponse.ifPresent(cryptos -> cryptosPlatformDistribution.add(new CryptosPlatformDistributionResponse(coinId, cryptos.coins())));
            });
        }

        return CollectionUtils.isNotEmpty(cryptosPlatformDistribution) ?
                Optional.of(cryptosPlatformDistribution) :
                Optional.empty();
    }

    private List<PlatformInfo> getPlatformInfo(Map<String, BigDecimal> balancePerPlatform, BigDecimal totalBalance) {
        List<PlatformInfo> platformsInfo = new ArrayList<>();

        balancePerPlatform.forEach((platform, platformBalance) -> {
            PlatformInfo platformInfo = new PlatformInfo(platform, getPercentage(platformBalance, totalBalance), platformBalance);
            platformsInfo.add(platformInfo);
        });

        return platformsInfo;
    }

    private double getPercentage(BigDecimal platformBalance, BigDecimal totalBalance) {
        return platformBalance
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalBalance, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
