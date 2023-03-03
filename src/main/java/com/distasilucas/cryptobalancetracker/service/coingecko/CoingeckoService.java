package com.distasilucas.cryptobalancetracker.service.coingecko;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;

import java.util.List;

public interface CoingeckoService {

    List<CoinInfo> retrieveAllCoins();
    CoinInfo retrieveCoinInfo(String coinId);
}
