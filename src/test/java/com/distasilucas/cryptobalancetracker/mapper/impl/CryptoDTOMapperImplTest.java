package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoDTOMapperImplTest {

    @Mock
    private PlatformRepository platformRepositoryMock;

    EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoDTOMapperImpl = new CryptoDTOMapperImpl(platformRepositoryMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var crypto = MockData.getCrypto("1234");
        var platform = MockData.getPlatform("Ledger");

        when(platformRepositoryMock.findById("1234")).thenReturn(Optional.of(platform));

        var cryptoDTO = cryptoDTOMapperImpl.mapFrom(crypto);

        assertAll(
                () -> assertEquals(crypto.getName(), cryptoDTO.coin_name()),
                () -> assertEquals(crypto.getTicker(), cryptoDTO.ticker()),
                () -> assertEquals(crypto.getQuantity(), cryptoDTO.quantity()),
                () -> assertEquals(crypto.getCoinId(), cryptoDTO.coinId()),
                () -> assertEquals(crypto.getPlatformId(), platform.getId())
        );
    }

    @Test
    void shouldMapSuccessfullyWithUnkownPlatform() {
        var crypto = MockData.getCrypto("1234");

        when(platformRepositoryMock.findById("1234")).thenReturn(Optional.empty());

        var cryptoDTO = cryptoDTOMapperImpl.mapFrom(crypto);

        assertAll(
                () -> assertEquals(crypto.getName(), cryptoDTO.coin_name()),
                () -> assertEquals(crypto.getTicker(), cryptoDTO.ticker()),
                () -> assertEquals(crypto.getQuantity(), cryptoDTO.quantity()),
                () -> assertEquals(crypto.getCoinId(), cryptoDTO.coinId()),
                () -> assertEquals(UNKNOWN, cryptoDTO.platform())
        );
    }

}