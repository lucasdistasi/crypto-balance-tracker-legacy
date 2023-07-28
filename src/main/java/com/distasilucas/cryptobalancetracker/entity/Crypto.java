package com.distasilucas.cryptobalancetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document("Cryptos")
@NoArgsConstructor
@AllArgsConstructor
public class Crypto {

    @Id
    private String id; // coinId from coingecko
    private String name;
    private String ticker;

    @Field("last_known_price")
    private BigDecimal lastKnownPrice;

    @Field("last_known_price_in_EUR")
    private BigDecimal lastKnownPriceInEUR;

    @Field("last_known_price_in_BTC")
    private BigDecimal lastKnownPriceInBTC;

    @Field("circulating_supply")
    private BigDecimal circulatingSupply;

    @Field("max_supply")
    private BigDecimal maxSupply;

    @Field("last_price_updated_at")
    private LocalDateTime lastPriceUpdatedAt;
}
