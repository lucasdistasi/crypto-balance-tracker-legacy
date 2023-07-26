package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.dashboard.CryptoBalanceResponseMapperImpl;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoBalanceResponseMapperImplTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    EntityMapper<CryptoBalanceResponse, List<UserCrypto>> cryptoBalanceResponseMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoBalanceResponseMapperImpl = new CryptoBalanceResponseMapperImpl(cryptoRepositoryMock, platformRepositoryMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var userCrypto = MockData.getUserCrypto();
        var crypto = MockData.getCrypto();
        var cryptos = Collections.singletonList(userCrypto);
        var platform = MockData.getPlatform("Ledger");

        when(cryptoRepositoryMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));
        when(platformRepositoryMock.findById("1234")).thenReturn(Optional.of(platform));

        var cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(cryptos);
        var totalBalance = cryptoBalanceResponse.totalBalance();
        var expectedBalance = totalBalance.setScale(2, RoundingMode.HALF_UP);

        assertAll(
                () -> assertEquals(cryptos.size(), cryptoBalanceResponse.coins().size()),
                () -> assertEquals(platform.getName(), cryptoBalanceResponse.coins().get(0).getPlatform()),
                () -> assertEquals(expectedBalance, totalBalance)
        );
    }

    @Test
    void shouldMapSuccessfullyWithUnknownPlatform() {
        var userCrypto = MockData.getUserCrypto("1234");
        var crypto = MockData.getCrypto();
        var userCryptos = Collections.singletonList(userCrypto);

        when(cryptoRepositoryMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(userCryptos);
        var totalBalance = cryptoBalanceResponse.totalBalance();
        var expectedBalance = totalBalance.setScale(2, RoundingMode.HALF_UP);

        assertAll(
                () -> assertEquals(userCryptos.size(), cryptoBalanceResponse.coins().size()),
                () -> assertEquals(UNKNOWN, cryptoBalanceResponse.coins().get(0).getPlatform()),
                () -> assertEquals(expectedBalance, totalBalance)
        );
    }

    @Test
    void shouldThrowApiException() {
        var crypto = MockData.getUserCrypto("Ledger");
        var allCryptos = Collections.singletonList(crypto);

        when(cryptoRepositoryMock.findById(crypto.getCryptoId())).thenReturn(Optional.empty());

        var apiException = assertThrows(ApiException.class,
                () -> cryptoBalanceResponseMapperImpl.mapFrom(allCryptos));

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, apiException.getHttpStatusCode()),
                () -> assertEquals(CRYPTO_NOT_FOUND, apiException.getMessage())
        );
    }
}