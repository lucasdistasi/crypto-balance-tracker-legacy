package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;

import java.util.Optional;

public interface UserCryptoService {

    UserCryptoResponse getCrypto(String id);
    Optional<PageCryptoResponse> getCryptos(int page);
    UserCryptoResponse saveUserCrypto(AddCryptoRequest cryptoRequest);
    UserCryptoResponse updateCrypto(UpdateCryptoRequest updateCryptoRequest, String id);
    void deleteCrypto(String id);
}
