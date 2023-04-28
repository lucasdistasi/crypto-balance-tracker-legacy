package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;

import java.util.List;

public record CryptosPlatformDistributionResponse(
        String crypto,
        List<CoinResponse> coins
) {
}
