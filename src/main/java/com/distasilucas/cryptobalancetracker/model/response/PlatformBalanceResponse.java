package com.distasilucas.cryptobalancetracker.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PlatformBalanceResponse {

    private List<PlatformInfo> platforms;
    private BigDecimal totalBalance;
}
