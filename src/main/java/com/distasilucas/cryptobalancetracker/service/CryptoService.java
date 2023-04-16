package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;

import java.util.List;
import java.util.Optional;

public interface CryptoService {

    CryptoResponse getCoin(String coinId);
    Optional<List<CryptoResponse>> getCoins();
    CryptoResponse addCoin(CryptoRequest cryptoRequest);
    CryptoResponse updateCoin(CryptoRequest cryptoRequest, String coinId);
    void deleteCoin(String coinId);
}
