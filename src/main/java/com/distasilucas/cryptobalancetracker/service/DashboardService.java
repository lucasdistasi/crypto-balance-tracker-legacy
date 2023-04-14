package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.PlatformBalanceResponse;

import java.util.Optional;

public interface DashboardService {

    Optional<CryptoBalanceResponse> retrieveCoinsBalances();
    Optional<CryptoBalanceResponse> retrieveCoinBalance(String coinId);
    Optional<CryptoPlatformBalanceResponse> retrieveCoinsBalanceByPlatform();
    Optional<PlatformBalanceResponse> getPlatformsBalances();
    Optional<CryptoBalanceResponse> getAllCoins(String platformName);
}
