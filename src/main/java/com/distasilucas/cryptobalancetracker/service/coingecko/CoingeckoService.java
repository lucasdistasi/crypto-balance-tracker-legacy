package com.distasilucas.cryptobalancetracker.service.coingecko;

import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;

import java.util.List;

public interface CoingeckoService {

    List<Coin> retrieveAllCryptos();
}
