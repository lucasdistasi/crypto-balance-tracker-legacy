package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.model.response.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CoinInfoResponseMapperImplTest {

    BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> coinInfoResponseMapper = new CoinInfoResponseMapperImpl();

    @Test
    void shouldMapSuccessfully() {
        Map<String, BigDecimal> map = new HashMap<>() {{
           put("ethereum", BigDecimal.valueOf(1));
        }};
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        List<CoinInfoResponse> coinInfoResponses = coinInfoResponseMapper.map()
                .apply(map, cryptoBalanceResponse);

        assertAll(
                () -> assertFalse(coinInfoResponses.isEmpty()),
                () -> assertEquals(map.size(), coinInfoResponses.size()),
                () -> assertEquals("ethereum", coinInfoResponses.get(0).getName())
        );
    }
}