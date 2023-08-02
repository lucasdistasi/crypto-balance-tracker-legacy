package com.distasilucas.cryptobalancetracker.model.response.platform;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PlatformInfo(
        String platformName,
        double percentage,
        BigDecimal balance
) {
}
