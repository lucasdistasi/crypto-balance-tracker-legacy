package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoPlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptosPlatformDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.PlatformsCryptoDistributionResponse;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformBalanceResponse;

import java.util.List;
import java.util.Optional;

public interface DashboardService {

    Optional<CryptoBalanceResponse> retrieveCryptosBalances();
    Optional<CryptoBalanceResponse> retrieveCryptoBalance(String cryptoId);
    Optional<CryptoPlatformBalanceResponse> retrieveCryptosBalanceByPlatform();
    Optional<PlatformBalanceResponse> getPlatformsBalances();
    Optional<CryptoBalanceResponse> getAllCryptos(String platformName);
    Optional<List<PlatformsCryptoDistributionResponse>> getPlatformsCryptoDistributionResponse();
    Optional<List<CryptosPlatformDistributionResponse>> getCryptosPlatformDistribution();
}
