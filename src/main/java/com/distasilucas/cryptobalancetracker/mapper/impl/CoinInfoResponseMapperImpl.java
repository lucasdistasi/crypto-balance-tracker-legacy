package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.comparators.DescendingBalanceComparator;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.model.response.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
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
            List<CoinResponse> coins = cryptoBalanceResponse.getCoins();

            coinByPlatform.forEach((coinName, coinTotalBalance) -> {
                List<CoinResponse> coinsResponse = coins.stream()
                        .filter(coin -> coin.getCoinInfo().getName().equals(coinName))
                        .toList();

                BigDecimal totalQuantity = coinsResponse.stream()
                        .map(CoinResponse::getQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Double totalPercentage = coinsResponse.stream()
                        .map(CoinResponse::getPercentage)
                        .reduce((double) 0, Double::sum);

                Set<String> platforms = coinsResponse.stream()
                        .map(CoinResponse::getPlatform)
                        .collect(Collectors.toSet());

                CoinInfoResponse coinInfoResponse = CoinInfoResponse.builder()
                        .quantity(totalQuantity)
                        .name(coinName)
                        .balance(coinTotalBalance)
                        .percentage(totalPercentage)
                        .platforms(platforms)
                        .build();

                coinInfoResponses.add(coinInfoResponse);
            });

            coinInfoResponses.sort(new DescendingBalanceComparator());

            return coinInfoResponses;
        };
    }
}
