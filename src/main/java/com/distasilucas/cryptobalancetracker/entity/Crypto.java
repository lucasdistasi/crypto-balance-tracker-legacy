package com.distasilucas.cryptobalancetracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static com.distasilucas.cryptobalancetracker.constant.Constants.QUANTITY_FRACTIONAL_MAX_LENGTH;
import static com.distasilucas.cryptobalancetracker.constant.Constants.QUANTITY_WHOLE_MAX_LENGTH;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Cryptos")
public class Crypto {

    @Id
    @Column
    private String name;
    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private String coinId;

    @Column(precision = QUANTITY_WHOLE_MAX_LENGTH, scale = QUANTITY_FRACTIONAL_MAX_LENGTH, nullable = false)
    private BigDecimal quantity;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

}

