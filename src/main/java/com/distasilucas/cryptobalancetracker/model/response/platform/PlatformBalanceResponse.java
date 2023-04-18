package com.distasilucas.cryptobalancetracker.model.response.platform;

import java.math.BigDecimal;
import java.util.List;

public record PlatformBalanceResponse(
        BigDecimal totalBalance,
        List<PlatformInfo> platforms
) {
}
