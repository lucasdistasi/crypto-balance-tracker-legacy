package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.dashboard.CryptoInfoResponseMapperImpl;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoBalanceResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CryptoInfoResponseMapperImplTest {

    BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CryptoInfoResponse>> cryptoInfoResponseMapper = new CryptoInfoResponseMapperImpl();

    @Test
    void shouldMapSuccessfully() {
        Map<String, BigDecimal> map = new HashMap<>() {{
           put("ethereum", BigDecimal.valueOf(1));
        }};
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();

        var cryptoInfoResponse = cryptoInfoResponseMapper.map()
                .apply(map, cryptoBalanceResponse);

        assertAll(
                () -> assertFalse(cryptoInfoResponse.isEmpty()),
                () -> assertEquals(map.size(), cryptoInfoResponse.size()),
                () -> assertEquals("ethereum", cryptoInfoResponse.get(0).name())
        );
    }
}