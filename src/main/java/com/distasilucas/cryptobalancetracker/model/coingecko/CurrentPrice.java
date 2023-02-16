package com.distasilucas.cryptobalancetracker.model.coingecko;

import java.io.Serializable;
import java.math.BigDecimal;

public record CurrentPrice(BigDecimal usd) implements Serializable {
}
