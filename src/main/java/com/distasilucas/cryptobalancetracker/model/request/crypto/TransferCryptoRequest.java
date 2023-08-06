package com.distasilucas.cryptobalancetracker.model.request.crypto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransferCryptoRequest {

    private String cryptoId;
    private BigDecimal quantityToTransfer;
    private boolean sendFullQuantity;
    private BigDecimal networkFee;
    private String toPlatform;

    public TransferCryptoRequest(String cryptoId, BigDecimal quantityToTransfer, BigDecimal networkFee, String toPlatform) {
        this.cryptoId = cryptoId;
        this.quantityToTransfer = quantityToTransfer;
        this.sendFullQuantity = false;
        this.networkFee = networkFee;
        this.toPlatform = toPlatform;
    }
}
