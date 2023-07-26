package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import java.util.List;

public record PlatformsCryptoDistributionResponse(
        String platform,
        List<CryptoResponse> cryptos
) {
}
