package com.distasilucas.cryptobalancetracker.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoDTO {

    private String name;

    private String coinId;
    private BigDecimal quantity;
    private BigDecimal balance;
    private String ticker;
}
