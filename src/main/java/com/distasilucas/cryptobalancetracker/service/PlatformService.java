package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.PlatformBalanceResponse;

import java.util.List;
import java.util.Optional;

public interface PlatformService {

    List<PlatformDTO> getAllPlatforms();
    PlatformDTO addPlatForm(PlatformDTO platform);
    Optional<PlatformBalanceResponse> getPlatformsBalances();
    Platform findPlatformByName(String platformName);
    PlatformDTO updatePlatform(PlatformDTO platformDTO, String platformName);
    void deletePlatform(String platformName);
    Optional<CryptoBalanceResponse> getAllCoins(String platformName);
}
