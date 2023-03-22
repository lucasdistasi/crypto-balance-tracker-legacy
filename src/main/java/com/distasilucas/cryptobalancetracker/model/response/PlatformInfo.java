package com.distasilucas.cryptobalancetracker.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PlatformInfo {

    private String platformName;
    private double percentage;
    private BigDecimal balance;
}