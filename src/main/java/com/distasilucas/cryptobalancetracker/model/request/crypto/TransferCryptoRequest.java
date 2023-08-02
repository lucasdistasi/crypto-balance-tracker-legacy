package com.distasilucas.cryptobalancetracker.model.request.crypto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransferCryptoRequest {

    private String cryptoId;
    private BigDecimal quantityToTransfer;
    private BigDecimal networkFee;
    private String toPlatform;
}
