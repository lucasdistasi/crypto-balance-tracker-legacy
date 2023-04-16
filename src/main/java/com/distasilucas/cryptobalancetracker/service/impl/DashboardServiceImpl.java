package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformInfo;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.DashboardService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CryptoRepository cryptoRepository;
    private final PlatformService platformService;
    private final EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImpl;
    private final BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> coinInfoResponseMapperImpl;

    @Override
    public Optional<CryptoBalanceResponse> retrieveCoinsBalances() {
        log.info("Retrieving coins balances");
        List<Crypto> allCoins = cryptoRepository.findAll();

        return CollectionUtils.isEmpty(allCoins) ?
                Optional.empty() :
                Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(allCoins));
    }

    @Override
    public Optional<CryptoBalanceResponse> retrieveCoinBalance(String coinId) {
        log.info("Retrieving balances for coin [{}]", coinId);
        Optional<List<Crypto>> allCoins = cryptoRepository.findAllByCoinId(coinId);

        return allCoins.isEmpty() || CollectionUtils.isEmpty(allCoins.get()) ?
                Optional.empty() :
                Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(allCoins.get()));
    }

    @Override
    public Optional<CryptoPlatformBalanceResponse> retrieveCoinsBalanceByPlatform() {
        log.info("Retrieving coins by platform");
        List<Crypto> allCoins = cryptoRepository.findAll();

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
        List<Crypto> cryptos = cryptoRepository.findAll();
        CryptoBalanceResponse cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(cryptos);
        List<CoinResponse> coins = cryptoBalanceResponse.coins();

        if (CollectionUtils.isNotEmpty(coins)) {
            Map<String, BigDecimal> balancePerPlatform = new HashMap<>();

            coins
                    .forEach(coin -> {
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
        log.info("Retrieving coins in platform {}", platformName);
        Platform platform = platformService.findPlatformByName(platformName);
        Optional<List<Crypto>> cryptos = cryptoRepository.findAllByPlatformId(platform.getId());

        Optional<CryptoBalanceResponse> cryptoBalanceResponse = Optional.empty();

        if (cryptos.isPresent() && CollectionUtils.isNotEmpty(cryptos.get())) {
            cryptoBalanceResponse = Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(cryptos.get()));
        }

        return cryptoBalanceResponse;
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
