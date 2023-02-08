package com.distasilucas.cryptobalancetracker.model.coingecko;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CurrentPrice implements Serializable {

    private BigDecimal usd;
}
