package com.distasilucas.cryptobalancetracker.model.response.platform;

import java.math.BigDecimal;

public record PlatformInfo(
        String platformName,
        double percentage,
        BigDecimal balance
) {
}
