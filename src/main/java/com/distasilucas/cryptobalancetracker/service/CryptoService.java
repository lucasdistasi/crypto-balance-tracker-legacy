package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;

public interface CryptoService {

    CryptoDTO addCoin(CryptoDTO cryptoDTO);
    CryptoDTO updateCoin(CryptoDTO cryptoDTO, String coinId);
    void deleteCoin(String coinId);
}
