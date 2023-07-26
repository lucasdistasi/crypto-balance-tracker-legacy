package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;

import java.util.Optional;

public interface UserCryptoService {

    CryptoResponse getCrypto(String id);
    Optional<PageCryptoResponse> getCryptos(int page);
    CryptoResponse saveUserCrypto(AddCryptoRequest cryptoRequest);
    CryptoResponse updateCrypto(UpdateCryptoRequest updateCryptoRequest, String id);
    void deleteCrypto(String id);
}
