package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;

import java.util.Optional;

public interface PlatformService {

    PlatformDTO addPlatForm(PlatformDTO platform);
    Platform findPlatform(String platformName);
    PlatformDTO updatePlatform(PlatformDTO platformDTO, String platformName);
    void deletePlatform(String platformName);
    Optional<CryptoBalanceResponse> getAllCoins(String platformName);
}
