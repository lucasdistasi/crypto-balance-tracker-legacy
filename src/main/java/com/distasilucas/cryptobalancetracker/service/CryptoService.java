package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;

import java.util.List;

public interface CryptoService<T, U> {

    T addCrypto(U input);
    List<CryptoBalanceResponse> retrieveCoinsBalances();
}
