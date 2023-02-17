package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CryptoDTOMapperImplTest {

    EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImpl = new CryptoDTOMapperImpl();

    @Test
    void shouldMapSuccessfully() {
        var platform = MockData.getPlatform("Ledger");
        var crypto = MockData.getCrypto(platform);

        var cryptoDTO = cryptoDTOMapperImpl.mapFrom(crypto);

        assertAll(
                () -> assertEquals(crypto.getName(), cryptoDTO.coin_name()),
                () -> assertEquals(crypto.getTicker(), cryptoDTO.ticker()),
                () -> assertEquals(crypto.getPlatform().getName(), cryptoDTO.platform()),
                () -> assertEquals(crypto.getQuantity(), cryptoDTO.quantity()),
                () -> assertEquals(crypto.getCoinId(), cryptoDTO.coinId())
        );
    }

}