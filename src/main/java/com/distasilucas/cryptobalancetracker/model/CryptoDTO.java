package com.distasilucas.cryptobalancetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoDTO {

    @JsonIgnore
    private String name;

    @JsonIgnore
    private String coinId;

    @JsonIgnore
    private BigDecimal quantity;
    private BigDecimal balance;
    private String ticker;
}
