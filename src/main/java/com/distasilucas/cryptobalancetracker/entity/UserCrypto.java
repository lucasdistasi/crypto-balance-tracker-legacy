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

@Getter
@Setter
@Builder
@Document("UserCrypto")
@NoArgsConstructor
@AllArgsConstructor
public class UserCrypto {

    @Id
    private String id; // random mongo id

    @Field("crypto_id")
    private String cryptoId; // coinId from coingecko
    private BigDecimal quantity;

    @Field("platform_id")
    private String platformId;

}

