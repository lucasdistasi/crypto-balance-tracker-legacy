package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.comparators.DescendingPercentageComparator;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
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

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;
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
                .map(this::getCoinResponse)
                .collect(Collectors.toList());

        BigDecimal totalMoney = getTotalMoney(coins);
        coins.forEach(crypto -> setPercentage(totalMoney, crypto));
        coins.sort(new DescendingPercentageComparator());
        BigDecimal totalBalance = totalMoney.setScale(2, RoundingMode.HALF_UP);

        return new CryptoBalanceResponse(totalBalance, coins);
    }

    private CoinResponse getCoinResponse(Crypto coin) {
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

    private BigDecimal getTotalMoney(List<CoinResponse> coins) {
        return coins.stream()
                .map(CoinResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    private CoinResponse getCoinResponse(Crypto coin, CoinInfo coinInfo) {
        BigDecimal quantity = coin.getQuantity();
        BigDecimal balance = coinInfo.getMarketData().currentPrice().usd().multiply(quantity);
        Optional<Platform> platform = platformRepository.findById(coin.getPlatformId());
        String platformName = platform.isPresent() ? platform.get().getName() : UNKNOWN;

        return new CoinResponse(coinInfo, quantity, balance, platformName);
    }

    private Function<Crypto, CoinInfo> mapCoinInfo() {
        return crypto -> {
            CurrentPrice currentPrice = new CurrentPrice(crypto.getLastKnownPrice());
            MarketData marketData = new MarketData(currentPrice, crypto.getTotalSupply(), crypto.getMaxSupply());

            CoinInfo coinInfo = new CoinInfo();
            coinInfo.setId(crypto.getId());
            coinInfo.setSymbol(crypto.getTicker());
            coinInfo.setName(crypto.getName());
            coinInfo.setMarketData(marketData);

            return coinInfo;
        };
    }
}
