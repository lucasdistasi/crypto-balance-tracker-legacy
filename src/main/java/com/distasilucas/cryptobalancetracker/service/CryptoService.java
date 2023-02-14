package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;

public interface CryptoService<U> {

    U addCoin(U input);
    CryptoBalanceResponse retrieveCoinsBalances();
    void deleteCoin(String coinName);
    U updateCoin(U input, String coinName);
}
