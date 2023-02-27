package com.distasilucas.cryptobalancetracker.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class CoinInfoResponse {

    private String name;
    private BigDecimal quantity;
    private BigDecimal balance;
    private double percentage;
    private Set<String> platforms;
}
