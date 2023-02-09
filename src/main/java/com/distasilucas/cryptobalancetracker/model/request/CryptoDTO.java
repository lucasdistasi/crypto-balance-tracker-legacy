package com.distasilucas.cryptobalancetracker.model.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoDTO {

    private BigDecimal quantity;

    private String ticker;
}
