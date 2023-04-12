package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoPlatformBalanceResponse;

import java.util.Optional;

public interface CryptoService {

    CryptoDTO addCoin(CryptoDTO cryptoDTO);
    CryptoDTO updateCoin(CryptoDTO cryptoDTO, String coinId);
    void deleteCoin(String coinId);
    Optional<CryptoBalanceResponse> retrieveCoinsBalances();
    Optional<CryptoBalanceResponse> retrieveCoinBalance(String coinId);
    Optional<CryptoPlatformBalanceResponse> retrieveCoinsBalanceByPlatform();
}
