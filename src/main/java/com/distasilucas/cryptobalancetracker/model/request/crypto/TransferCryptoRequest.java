package com.distasilucas.cryptobalancetracker.model.request.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TransferCryptoRequest {

    private String cryptoId;
    private BigDecimal quantityToTransfer;
    private BigDecimal networkFee;
    private String fromPlatform;
    private String toPlatform;
}
