package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;

public interface CryptoService<U> {

    U addCoin(U input);
    CryptoBalanceResponse retrieveCoinsBalances();
}
