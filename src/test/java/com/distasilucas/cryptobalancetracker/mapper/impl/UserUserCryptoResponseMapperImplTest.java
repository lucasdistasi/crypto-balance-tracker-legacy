package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.crypto.UserCryptoResponseMapperImpl;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUserCryptoResponseMapperImplTest {

    @Mock
    private CryptoService cryptoServiceMock;

    @Mock
    private PlatformService platformServiceMock;

    EntityMapper<UserCryptoResponse, UserCrypto> cryptoResponseMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoResponseMapperImpl = new UserCryptoResponseMapperImpl(cryptoServiceMock, platformServiceMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var userCrypto = MockData.getUserCrypto("1234");
        var crypto = MockData.getCrypto();
        var platform = MockData.getPlatform("Ledger");

        when(platformServiceMock.findById("1234")).thenReturn(Optional.of(platform));
        when(cryptoServiceMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

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

        when(platformServiceMock.findById("1234")).thenReturn(Optional.empty());
        when(cryptoServiceMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var cryptoResponse = cryptoResponseMapperImpl.mapFrom(userCrypto);

        assertAll(
                () -> assertEquals(userCrypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertNotNull(cryptoResponse.getId()),
                () -> assertEquals(UNKNOWN, cryptoResponse.getPlatform())
        );
    }

    @Test
    void shouldThrowCryptoNotFoundException() {
        var userCrypto = MockData.getUserCrypto("1234");
        var platform = MockData.getPlatform("Ledger");

        when(platformServiceMock.findById("1234")).thenReturn(Optional.of(platform));
        when(cryptoServiceMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.empty());

        var exception = assertThrows(CryptoNotFoundException.class, () -> cryptoResponseMapperImpl.mapFrom(userCrypto));

        assertEquals(CRYPTO_NOT_FOUND, exception.getErrorMessage());
    }
}