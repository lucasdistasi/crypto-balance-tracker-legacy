package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;

public interface CryptoService<T, U> {

    T addCrypto(U input);
    CryptoBalanceResponse retrieveCoinsBalances();
}
