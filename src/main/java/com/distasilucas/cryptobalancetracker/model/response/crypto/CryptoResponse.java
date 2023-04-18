package com.distasilucas.cryptobalancetracker.model.response.crypto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptoResponse {

    private String coinId;
    private String coinName;
    private String platform;
    private BigDecimal quantity;
}
