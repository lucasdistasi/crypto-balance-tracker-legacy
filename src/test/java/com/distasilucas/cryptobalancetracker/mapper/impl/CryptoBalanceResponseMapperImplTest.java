package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.ApiException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.mapper.impl.dashboard.CryptoBalanceResponseMapperImpl;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoBalanceResponseMapperImplTest {

    @Mock
    CryptoService cryptoServiceMock;

    @Mock
    PlatformService platformServiceMock;

    EntityMapper<CryptoBalanceResponse, List<UserCrypto>> cryptoBalanceResponseMapperImpl;

    @BeforeEach
    void setUp() {
        cryptoBalanceResponseMapperImpl = new CryptoBalanceResponseMapperImpl(cryptoServiceMock, platformServiceMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var userCrypto = MockData.getUserCrypto();
        var crypto = MockData.getCrypto();
        var cryptos = Collections.singletonList(userCrypto);
        var platform = MockData.getPlatform("Ledger");

        when(cryptoServiceMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));
        when(platformServiceMock.findById("1234")).thenReturn(Optional.of(platform));

        var cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(cryptos);
        var totalBalance = cryptoBalanceResponse.totalBalance();
        var expectedBalance = totalBalance.setScale(2, RoundingMode.HALF_UP);

        assertAll(
                () -> assertEquals(cryptos.size(), cryptoBalanceResponse.cryptos().size()),
                () -> assertEquals(platform.getName(), cryptoBalanceResponse.cryptos().get(0).getPlatform()),
                () -> assertEquals(expectedBalance, totalBalance)
        );
    }

    @Test
    void shouldThrowPlatformNotFoundExceptionWhenMapping() {
        var userCrypto = MockData.getUserCrypto("1234");
        var crypto = MockData.getCrypto();
        var userCryptos = Collections.singletonList(userCrypto);

        when(cryptoServiceMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var exception = assertThrows(PlatformNotFoundException.class,
                () -> cryptoBalanceResponseMapperImpl.mapFrom(userCryptos));

        assertEquals(PLATFORM_NOT_FOUND, exception.getErrorMessage());
    }

    @Test
    void shouldThrowCryptoNotFoundException() {
        var crypto = MockData.getUserCrypto("Ledger");
        var allCryptos = Collections.singletonList(crypto);

        when(cryptoServiceMock.findById(crypto.getCryptoId())).thenReturn(Optional.empty());

        var apiException = assertThrows(CryptoNotFoundException.class,
                () -> cryptoBalanceResponseMapperImpl.mapFrom(allCryptos));

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, apiException.getHttpStatusCode()),
                () -> assertEquals(CRYPTO_NOT_FOUND, apiException.getMessage())
        );
    }
}