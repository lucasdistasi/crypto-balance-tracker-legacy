package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.crypto.CryptoResponseMapperImpl;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.WebSocketHandshakeException;
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

    @Mock
    private CryptoRepository cryptoRepositoryMock;

    EntityMapper<CryptoResponse, UserCrypto> cryptoResponseMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoResponseMapperImpl = new CryptoResponseMapperImpl(cryptoRepositoryMock, platformRepositoryMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var userCrypto = MockData.getUserCrypto("1234");
        var crypto = MockData.getCrypto();
        var platform = MockData.getPlatform("Ledger");

        when(platformRepositoryMock.findById("1234")).thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var cryptoResponse = cryptoResponseMapperImpl.mapFrom(userCrypto);

        assertAll(
                () -> assertEquals(userCrypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertNotNull(cryptoResponse.getId()),
                () -> assertEquals(userCrypto.getPlatformId(), platform.getId())
        );
    }

    @Test
    void shouldMapSuccessfullyWithUnknownPlatform() {
        var userCrypto = MockData.getUserCrypto("1234");
        var crypto = MockData.getCrypto();

        when(platformRepositoryMock.findById("1234")).thenReturn(Optional.empty());
        when(cryptoRepositoryMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var cryptoResponse = cryptoResponseMapperImpl.mapFrom(userCrypto);

        assertAll(
                () -> assertEquals(userCrypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertNotNull(cryptoResponse.getId()),
                () -> assertEquals(UNKNOWN, cryptoResponse.getPlatform())
        );
    }

}