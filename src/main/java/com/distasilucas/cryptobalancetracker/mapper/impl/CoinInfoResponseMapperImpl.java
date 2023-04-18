package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.comparator.DescendingBalanceComparator;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class CoinInfoResponseMapperImpl implements BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> {

    @Override
    public BiFunction<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> map() {
        List<CoinInfoResponse> coinInfoResponses = new ArrayList<>();

        return (coinByPlatform, cryptoBalanceResponse) -> {
            List<CoinResponse> coins = cryptoBalanceResponse.coins();

            coinByPlatform.forEach((coinName, coinTotalBalance) -> {
                List<CoinResponse> coinsResponse = getCoinsResponse(coins, coinName);
                BigDecimal totalQuantity = getTotalQuantity(coinsResponse);
                Double totalPercentage = getTotalPercentage(coinsResponse);
                Set<String> platforms = getPlatforms(coinsResponse);

                CoinInfoResponse coinInfoResponse = new CoinInfoResponse(coinName, totalQuantity, coinTotalBalance,
                        totalPercentage, platforms);

                coinInfoResponses.add(coinInfoResponse);
            });

            coinInfoResponses.sort(new DescendingBalanceComparator());

            return coinInfoResponses;
        };
    }

    private List<CoinResponse> getCoinsResponse(List<CoinResponse> coins, String coinName) {
        return coins.stream()
                .filter(coin -> coin.getCoinInfo().getName().equals(coinName))
                .toList();
    }

    private BigDecimal getTotalQuantity(List<CoinResponse> coinsResponse) {
        return coinsResponse.stream()
                .map(CoinResponse::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Double getTotalPercentage(List<CoinResponse> coinsResponse) {
        return coinsResponse.stream()
                .map(CoinResponse::getPercentage)
                .reduce((double) 0, Double::sum);
    }

    private Set<String> getPlatforms(List<CoinResponse> coinsResponse) {
        return coinsResponse.stream()
                .map(CoinResponse::getPlatform)
                .collect(Collectors.toSet());
    }
}
