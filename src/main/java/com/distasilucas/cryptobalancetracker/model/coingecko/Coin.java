package com.distasilucas.cryptobalancetracker.model.coingecko;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public sealed class Coin permits CoinInfo {

    private String id;
    private String symbol;
    private String name;
}
