package com.distasilucas.cryptobalancetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String id;
    private String name;
    private String ticker;
    private String coinId;
    private BigDecimal quantity;
    private String platformId;
    private BigDecimal lastKnownPrice;
    private BigDecimal lastKnownPriceInEUR;
    private BigDecimal lastKnownPriceInBTC;
    private BigDecimal totalSupply;
    private BigDecimal maxSupply;
    private LocalDateTime lastPriceUpdatedAt;

}

