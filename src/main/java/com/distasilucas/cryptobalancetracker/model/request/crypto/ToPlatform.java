package com.distasilucas.cryptobalancetracker.model.request.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToPlatform {

    private BigDecimal newQuantity;
}
