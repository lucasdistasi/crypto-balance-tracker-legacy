package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;

import java.util.List;
import java.util.Optional;

public interface CryptoService {

    CryptoResponse getCoin(String coinId);
    Optional<List<CryptoResponse>> getCoins();
    CryptoResponse addCoin(AddCryptoRequest cryptoRequest);
    CryptoResponse updateCoin(UpdateCryptoRequest updateCryptoRequest, String coinId);
    void deleteCoin(String coinId);
}
