package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;

import java.util.Optional;

public interface CryptoService {

    CryptoResponse getCoin(String coinId);
    Optional<PageCryptoResponse> getCoins(int page);
    CryptoResponse addCoin(AddCryptoRequest cryptoRequest);
    CryptoResponse updateCoin(UpdateCryptoRequest updateCryptoRequest, String coinId);
    void deleteCoin(String coinId);
}
