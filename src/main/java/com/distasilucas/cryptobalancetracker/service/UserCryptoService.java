package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;

import java.util.List;
import java.util.Optional;

public interface UserCryptoService {

    Optional<UserCrypto> findFirstByCryptoId(String cryptoId);
    Optional<List<UserCrypto>> findAllByCryptoId(String cryptoId);
    Optional<UserCrypto> findById(String id);
    Optional<UserCrypto> findByCryptoIdAndPlatformId(String cryptoId, String platformId);
    List<UserCrypto> findAll();
    Optional<List<UserCrypto>> findAllByPlatformId(String platformId);
    void saveUserCrypto(UserCrypto userCrypto);
    void saveAll(List<UserCrypto> userCryptos);
    UserCryptoResponse getUserCryptoResponse(String id);
    Optional<PageCryptoResponse> getCryptos(int page);
    UserCryptoResponse saveUserCrypto(AddCryptoRequest cryptoRequest);
    UserCryptoResponse updateUserCrypto(UpdateCryptoRequest updateCryptoRequest, String id);
    void deleteUserCrypto(String id);
    void deleteUserCrypto(UserCrypto userCrypto);
}
