package com.distasilucas.cryptobalancetracker.model.coingecko;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public sealed class Coin implements Serializable permits CoinInfo {

    private String id;
    private String symbol;
    private String name;
}
