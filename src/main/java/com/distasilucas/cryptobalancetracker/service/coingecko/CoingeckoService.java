package com.distasilucas.cryptobalancetracker.service.coingecko;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCrypto;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCryptoInfo;

import java.util.List;

public interface CoingeckoService {

    List<CoingeckoCrypto> retrieveAllCoingeckoCryptos();
    CoingeckoCryptoInfo retrieveCoingeckoCryptoInfo(String coinId);
}
