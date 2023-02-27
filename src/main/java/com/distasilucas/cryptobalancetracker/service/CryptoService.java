package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoPlatformBalanceResponse;

import java.util.Optional;

public interface CryptoService<U> {

    U addCoin(U input);
    Optional<CryptoBalanceResponse> retrieveCoinsBalances();
    Optional<CryptoBalanceResponse> retrieveCoinBalance(String coinId);
    Optional<CryptoPlatformBalanceResponse> retrieveCoinsBalanceByPlatform();
}
