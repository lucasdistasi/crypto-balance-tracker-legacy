package com.distasilucas.cryptobalancetracker.model.coingecko;

import java.io.Serializable;

public record Coin(String id, String symbol, String name) implements Serializable {
}
