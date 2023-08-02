package com.distasilucas.cryptobalancetracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FromPlatform {

    private BigDecimal networkFee;
    private BigDecimal quantityToTransfer;
    private BigDecimal totalToSubtract;
    private BigDecimal quantityToSendReceive;
    private BigDecimal remainingCryptoQuantity;
}
