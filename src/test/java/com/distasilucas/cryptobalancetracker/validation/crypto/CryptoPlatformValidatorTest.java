package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoPlatformValidatorTest {

    @Mock
    CoingeckoService coingeckoServiceMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    EntityValidation<AddCryptoRequest> addCryptoRequestEntityValidation;
    EntityValidation<UpdateCryptoRequest> updateCryptoRequestEntityValidation;

    @BeforeEach
    void setUp() {
        addCryptoRequestEntityValidation = new CryptoPlatformValidator<>(coingeckoServiceMock, cryptoRepositoryMock, platformRepositoryMock);
        updateCryptoRequestEntityValidation = new CryptoPlatformValidator<>(coingeckoServiceMock, cryptoRepositoryMock, platformRepositoryMock);
    }

    @Test
    void shouldValidateSuccessfullyForAdd() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platform = MockData.getPlatform("LEDGER");
        var coin = new Coin("ethereum", "ETH", "Ethereum");

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.singletonList(coin));
        when(platformRepositoryMock.findByName(addCryptoRequest.getPlatform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findByNameAndPlatformId(addCryptoRequest.getCoinName(), platform.getId()))
                .thenReturn(Optional.empty());

        addCryptoRequestEntityValidation.validate(addCryptoRequest);
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenValidatingForAdd() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platform = MockData.getPlatform("Ledger");
        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, "Ethereum");

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platform));

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> addCryptoRequestEntityValidation.validate(addCryptoRequest));

        assertEquals(expectedMessage, coinNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowPlatformNotFoundExceptionWhenValidatingForAdd() {
        var addCryptoRequest = MockData.getAddCryptoRequest();

        when(platformRepositoryMock.findByName(addCryptoRequest.getPlatform().toUpperCase()))
                .thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> addCryptoRequestEntityValidation.validate(addCryptoRequest)
        );

        var message = String.format(PLATFORM_NOT_FOUND, addCryptoRequest.getPlatform());

        assertEquals(message, platformNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowApiValidationExceptionWhenValidatingForAdd() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platform = MockData.getPlatform("LEDGER");
        var crypto = MockData.getCrypto(platform.getId());
        var coin = new Coin("ethereum", "ETH", "Ethereum");

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.singletonList(coin));
        when(platformRepositoryMock.findByName(addCryptoRequest.getPlatform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findByNameAndPlatformId(addCryptoRequest.getCoinName(), platform.getId()))
                .thenReturn(Optional.of(crypto));

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> addCryptoRequestEntityValidation.validate(addCryptoRequest)
        );

        var message = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

        assertEquals(message, apiValidationException.getErrorMessage());
    }

    @Test
    void shouldValidateSuccessfullyWhenUpdateWithSamePlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.3), "Safepal");
        var platform = MockData.getPlatform("Safepal");
        var crypto = MockData.getCrypto("1234");

        when(platformRepositoryMock.findByName("SAFEPAL")).thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.of(crypto));

        updateCryptoRequestEntityValidation.validate(updateCryptoRequest);
    }

    @Test
    void shouldValidateSuccessfullyWhenUpdateWithDifferentPlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.35), "Trezor");
        var platform = MockData.getPlatform("Trezor");
        var crypto = MockData.getCrypto("5678");

        when(platformRepositoryMock.findByName("TREZOR")).thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.of(crypto));
        when(cryptoRepositoryMock.findByNameAndPlatformId("Bitcoin", "1234")).thenReturn(Optional.empty());

        updateCryptoRequestEntityValidation.validate(updateCryptoRequest);
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenUpdateWithDifferentPlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.35), "Safepal");
        var platform = MockData.getPlatform("Safepal");
        var expectedMessage = String.format(COIN_ID_NOT_FOUND, "ABC1234");

        when(platformRepositoryMock.findByName("SAFEPAL")).thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> updateCryptoRequestEntityValidation.validate(updateCryptoRequest));

        assertEquals(expectedMessage, coinNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowApiValidationExceptionWhenUpdateWithDifferentPlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.35), "Trezor");
        var platform = MockData.getPlatform("Trezor");
        var crypto = MockData.getCrypto("5678");
        var anotherCrypto = MockData.getCrypto("5678");
        var expectedMessage = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

        when(platformRepositoryMock.findByName("TREZOR")).thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.of(crypto));
        when(cryptoRepositoryMock.findByNameAndPlatformId("Bitcoin", "1234")).thenReturn(Optional.of(anotherCrypto));

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> updateCryptoRequestEntityValidation.validate(updateCryptoRequest));

        assertEquals(expectedMessage, apiValidationException.getErrorMessage());
    }
}