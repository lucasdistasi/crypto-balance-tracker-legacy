package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.comparator.DescendingPercentageComparator;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoBalanceResponseMapperImpl implements EntityMapper<CryptoBalanceResponse, List<Crypto>> {

    private final PlatformRepository platformRepository;
    private final CryptoRepository cryptoRepository;

    @Override
    public CryptoBalanceResponse mapFrom(List<Crypto> input) {
        List<CoinResponse> coins = input.stream()
                .map(this::mapCoinResponse)
                .collect(Collectors.toList());

        BigDecimal totalMoneyInUSD = getTotalMoneyInUSD(coins);
        BigDecimal totalMoneyInEUR = getTotalMoneyInEUR(coins);
        BigDecimal totalMoneyInBTC = getTotalMoneyInBTC(coins);
        coins.forEach(crypto -> setPercentage(totalMoneyInUSD, crypto));
        coins.sort(new DescendingPercentageComparator());
        BigDecimal totalUSDBalance = totalMoneyInUSD.setScale(2, RoundingMode.HALF_UP);

        return new CryptoBalanceResponse(totalUSDBalance, totalMoneyInEUR, totalMoneyInBTC, coins);
    }

    private CoinResponse mapCoinResponse(Crypto coin) {
        Optional<Crypto> crypto = cryptoRepository.findById(coin.getId());

        if (crypto.isEmpty()) {
            String message = String.format(COIN_NAME_NOT_FOUND, coin.getName());

            throw new ApiException(message, HttpStatus.NOT_FOUND);
        }

        CoinInfo coinInfo = mapCoinInfo().apply(crypto.get());

        return getCoinResponse(coin, coinInfo);
    }

    private void setPercentage(BigDecimal totalMoney, CoinResponse coinResponse) {
        double percentage = coinResponse.getBalance()
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMoney, RoundingMode.HALF_UP)
                .doubleValue();

        coinResponse.setPercentage(percentage);
    }

    private BigDecimal getTotalMoneyInUSD(List<CoinResponse> coins) {
        return coins.stream()
                .map(CoinResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    private BigDecimal getTotalMoneyInEUR(List<CoinResponse> coins) {
        return coins.stream()
                .map(CoinResponse::getBalanceInEUR)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    private BigDecimal getTotalMoneyInBTC(List<CoinResponse> coins) {
        return coins.stream()
                .map(CoinResponse::getBalanceInBTC)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    private CoinResponse getCoinResponse(Crypto coin, CoinInfo coinInfo) {
        BigDecimal quantity = coin.getQuantity();
        BigDecimal balanceInUSD = coinInfo.getMarketData().currentPrice().usd().multiply(quantity);
        BigDecimal balanceInEUR = coinInfo.getMarketData().currentPrice().eur().multiply(quantity);
        BigDecimal balanceInBTC = coinInfo.getMarketData().currentPrice().btc().multiply(quantity);
        Optional<Platform> platform = platformRepository.findById(coin.getPlatformId());
        String platformName = platform.isPresent() ? platform.get().getName() : UNKNOWN;

        return new CoinResponse(coin.getId(), coinInfo, quantity, balanceInUSD, balanceInEUR, balanceInBTC, platformName);
    }

    private Function<Crypto, CoinInfo> mapCoinInfo() {
        return crypto -> {
            CurrentPrice currentPrice = new CurrentPrice(crypto.getLastKnownPrice(), crypto.getLastKnownPriceInEUR(), crypto.getLastKnownPriceInBTC());
            MarketData marketData = new MarketData(currentPrice, crypto.getTotalSupply(), crypto.getMaxSupply());

            CoinInfo coinInfo = new CoinInfo();
            coinInfo.setId(crypto.getCoinId());
            coinInfo.setSymbol(crypto.getTicker());
            coinInfo.setName(crypto.getName());
            coinInfo.setMarketData(marketData);

            return coinInfo;
        };
    }
}
