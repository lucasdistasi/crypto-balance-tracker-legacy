package com.distasilucas.cryptobalancetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.Constants.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Cryptos")
public class Crypto {

    @Id
    @Column
    private String ticker;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String coinId;

    @Column(precision = QUANTITY_WHOLE_MAX_LENGTH, scale = QUANTITY_FRACTIONAL_MAX_LENGTH, nullable = false)
    private BigDecimal quantity;

}

