package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
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

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM_CRYPTO;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoPlatformValidatorTest {

    @Mock
    CoingeckoService coingeckoServiceMock;

    @Mock
    UserCryptoRepository userCryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    EntityValidation<AddCryptoRequest> addCryptoRequestEntityValidation;
    EntityValidation<UpdateCryptoRequest> updateCryptoRequestEntityValidation;

    @BeforeEach
    void setUp() {
        addCryptoRequestEntityValidation = new CryptoPlatformValidator<>(coingeckoServiceMock, platformRepositoryMock, userCryptoRepositoryMock);
        updateCryptoRequestEntityValidation = new CryptoPlatformValidator<>(coingeckoServiceMock, platformRepositoryMock, userCryptoRepositoryMock);
    }

    @Test
    void shouldValidateSuccessfullyForAdd() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platform = MockData.getPlatform("LEDGER");
        var coin = new Coin("ethereum", "ETH", "Ethereum");

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.singletonList(coin));
        when(platformRepositoryMock.findByName(addCryptoRequest.getPlatform().toUpperCase()))
                .thenReturn(Optional.of(platform));

        addCryptoRequestEntityValidation.validate(addCryptoRequest);
    }

    @Test
    void shouldThrowCryptoNotFoundExceptionWhenValidatingForAdd() {
        var addCryptoRequest = MockData.getAddCryptoRequest();
        var platform = MockData.getPlatform("Ledger");
        var expectedMessage = String.format(CRYPTO_NAME_NOT_FOUND, "Ethereum");

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platform));

        var cryptoNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> addCryptoRequestEntityValidation.validate(addCryptoRequest));

        assertEquals(expectedMessage, cryptoNotFoundException.getErrorMessage());
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
        var coin = new Coin("ethereum", "ETH", "Ethereum");
        var userCrypto = MockData.getUserCrypto();

        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.singletonList(coin));
        when(platformRepositoryMock.findByName(addCryptoRequest.getPlatform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(userCryptoRepositoryMock.findByCryptoIdAndPlatformId(coin.getId(), platform.getId()))
                .thenReturn(Optional.of(userCrypto));

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> addCryptoRequestEntityValidation.validate(addCryptoRequest)
        );

        var message = String.format(DUPLICATED_PLATFORM_CRYPTO, platform.getName());

        assertEquals(message, apiValidationException.getErrorMessage());
    }

    @Test
    void shouldValidateSuccessfullyWhenUpdateWithSamePlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.3), "Safepal");
        var platform = MockData.getPlatform("Safepal");
        var crypto = MockData.getUserCrypto("1234");

        when(platformRepositoryMock.findByName("SAFEPAL")).thenReturn(Optional.of(platform));
        when(userCryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.of(crypto));

        updateCryptoRequestEntityValidation.validate(updateCryptoRequest);
    }

    @Test
    void shouldValidateSuccessfullyWhenUpdateWithDifferentPlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.35), "Trezor");
        var platform = MockData.getPlatform("Trezor");
        var crypto = MockData.getUserCrypto("5678");

        when(platformRepositoryMock.findByName("TREZOR")).thenReturn(Optional.of(platform));
        when(userCryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.of(crypto));

        updateCryptoRequestEntityValidation.validate(updateCryptoRequest);
    }

    @Test
    void shouldThrowCryptoNotFoundExceptionWhenUpdateWithDifferentPlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.35), "Safepal");
        var platform = MockData.getPlatform("Safepal");
        var expectedMessage = String.format(CRYPTO_ID_NOT_FOUND, "ABC1234");

        when(platformRepositoryMock.findByName("SAFEPAL")).thenReturn(Optional.of(platform));
        when(userCryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.empty());

        var cryptoNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> updateCryptoRequestEntityValidation.validate(updateCryptoRequest));

        assertEquals(expectedMessage, cryptoNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowApiValidationExceptionWhenUpdateWithDifferentPlatform() {
        var updateCryptoRequest = new UpdateCryptoRequest("ABC1234", BigDecimal.valueOf(0.35), "Trezor");
        var platform = MockData.getPlatform("Trezor");
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .platformId(platform.getId())
                .build();
        var expectedMessage = String.format(DUPLICATED_PLATFORM_CRYPTO, platform.getName());

        when(platformRepositoryMock.findByName("TREZOR"))
                .thenReturn(Optional.of(platform));
        when(userCryptoRepositoryMock.findById(updateCryptoRequest.getCryptoId()))
                .thenReturn(Optional.of(userCrypto));
        when(userCryptoRepositoryMock.findByCryptoIdAndPlatformId(userCrypto.getCryptoId(), platform.getId()))
                .thenReturn(Optional.of(userCrypto));

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> updateCryptoRequestEntityValidation.validate(updateCryptoRequest));

        assertEquals(expectedMessage, apiValidationException.getErrorMessage());
    }
}