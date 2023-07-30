package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Crypto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CryptoService {

    Optional<Crypto> findById(String cryptoId);
    List<Crypto> findAllById(List<String> cryptoIds);
    void saveAllCryptos(List<Crypto> cryptosToSave);
    void saveCryptoIfNotExists(String cryptoId);
    void deleteCryptoIfNotUsed(String cryptoId);
    List<Crypto> findTopNCryptosOrderByLastPriceUpdatedAtAsc(LocalDateTime dateFilter, int limit);
}
