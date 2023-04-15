package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;

import java.util.List;
import java.util.Optional;

public interface CryptoService {

    Optional<CryptoDTO> getCoin(String coinId);
    Optional<List<CryptoDTO>> getCoins();
    CryptoDTO addCoin(CryptoDTO cryptoDTO);
    CryptoDTO updateCoin(CryptoDTO cryptoDTO, String coinId);
    void deleteCoin(String coinId);
}
