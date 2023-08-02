package com.distasilucas.cryptobalancetracker.model.response.platform;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PlatformBalanceResponse(
        BigDecimal totalBalance,
        List<PlatformInfo> platforms
) {
}
