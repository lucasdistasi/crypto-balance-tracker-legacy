package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CryptosPlatformDistributionResponse(
        String cryptoId,
        List<CryptoResponse> cryptos
) {
}
