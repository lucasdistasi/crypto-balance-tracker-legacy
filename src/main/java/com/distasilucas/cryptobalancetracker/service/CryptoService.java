package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;

public interface CryptoService<T, U> {

    T addCoin(U input);
    CryptoBalanceResponse retrieveCoinsBalances();
    void deleteCoin(String coinName);
    T updateCoin(U input, String coinName);
}
