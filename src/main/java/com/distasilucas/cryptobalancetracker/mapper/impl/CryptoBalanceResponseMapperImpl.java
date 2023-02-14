package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoBalanceResponseMapperImpl implements EntityMapper<CryptoBalanceResponse, List<Crypto>> {

    private final CoingeckoService coingeckoService;

    @Override
    public CryptoBalanceResponse mapFrom(List<Crypto> input) {
        log.info("Mapping from List<Crypto> to CryptoBalanceResponse");

        List<CoinResponse> coins = input.stream()
                .map(this::getCoinResponse)
                .toList();

        BigDecimal totalMoney = getTotalMoney(coins);
        coins.forEach(crypto -> setPercentage(totalMoney, crypto));
        BigDecimal totalBalance = totalMoney.setScale(2, RoundingMode.HALF_UP);

        return new CryptoBalanceResponse(totalBalance, coins);
    }

    private CoinResponse getCoinResponse(Crypto coin) {
        CoinInfo coinInfo = coingeckoService.retrieveCoinInfo(coin.getCoinId());
        BigDecimal quantity = coin.getQuantity();
        BigDecimal balance = coinInfo.getMarketData().getCurrentPrice().getUsd().multiply(quantity);
        String platformName = coin.getPlatform().getName();

        return new CoinResponse(coinInfo, quantity, balance, platformName);
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
}
