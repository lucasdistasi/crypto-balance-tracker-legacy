package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoResponseMapperImplTest {

    @Mock
    private PlatformRepository platformRepositoryMock;

    EntityMapper<CryptoResponse, Crypto> cryptoResponseMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoResponseMapperImpl = new CryptoResponseMapperImpl(platformRepositoryMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var crypto = MockData.getCrypto("1234");
        var platform = MockData.getPlatform("Ledger");

        when(platformRepositoryMock.findById("1234")).thenReturn(Optional.of(platform));

        var cryptoResponse = cryptoResponseMapperImpl.mapFrom(crypto);

        assertAll(
                () -> assertEquals(crypto.getName(), cryptoResponse.getCoinName()),
                () -> assertEquals(crypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertNotNull(cryptoResponse.getCoinId()),
                () -> assertEquals(crypto.getPlatformId(), platform.getId())
        );
    }

    @Test
    void shouldMapSuccessfullyWithUnknownPlatform() {
        var crypto = MockData.getCrypto("1234");

        when(platformRepositoryMock.findById("1234")).thenReturn(Optional.empty());

        var cryptoResponse = cryptoResponseMapperImpl.mapFrom(crypto);

        assertAll(
                () -> assertEquals(crypto.getName(), cryptoResponse.getCoinName()),
                () -> assertEquals(crypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertNotNull(cryptoResponse.getCoinId()),
                () -> assertEquals(UNKNOWN, cryptoResponse.getPlatform())
        );
    }

}