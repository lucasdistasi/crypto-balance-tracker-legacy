package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import java.util.List;

public record CryptosPlatformDistributionResponse(
        String cryptoId,
        List<CryptoResponse> cryptos
) {
}
