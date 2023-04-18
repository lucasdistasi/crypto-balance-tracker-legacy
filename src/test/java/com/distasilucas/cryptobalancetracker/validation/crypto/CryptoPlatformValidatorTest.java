package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoPlatformValidatorTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    EntityValidation<CryptoRequest> entityValidation;

    @BeforeEach
    void setUp() {
        entityValidation = new CryptoPlatformValidator(cryptoRepositoryMock, platformRepositoryMock);
    }

    @Test
    void shouldValidateSuccessfully() {
        var cryptoRequest = MockData.getCryptoRequest();
        var platform = MockData.getPlatform("LEDGER");

        when(platformRepositoryMock.findByName(cryptoRequest.platform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findByNameAndPlatformId(cryptoRequest.coin_name(), platform.getId()))
                .thenReturn(Optional.empty());

        entityValidation.validate(cryptoRequest);
    }

    @Test
    void shouldValidateSuccessfullyWithExistingCryptoInAnotherPlatform() {
        var cryptoRequest = MockData.getCryptoRequest();
        var platform = MockData.getPlatform("Binance");
        var anotherPlatform = MockData.getPlatform("Binance");
        var crypto = MockData.getCrypto(platform.getId());

        when(platformRepositoryMock.findByName(cryptoRequest.platform().toUpperCase()))
                .thenReturn(Optional.of(anotherPlatform));
        when(cryptoRepositoryMock.findByNameAndPlatformId(cryptoRequest.coin_name(), platform.getId()))
                .thenReturn(Optional.of(crypto));

        entityValidation.validate(cryptoRequest);
    }

    @Test
    void shouldThrowPlatformNotFoundExceptionWhenValidating() {
        var cryptoRequest = MockData.getCryptoRequest();

        when(platformRepositoryMock.findByName(cryptoRequest.platform().toUpperCase()))
                .thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> entityValidation.validate(cryptoRequest)
        );

        var message = String.format(PLATFORM_NOT_FOUND, cryptoRequest.platform());

        assertEquals(message, platformNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowApiValidationExceptionWhenValidating() {
        var cryptoRequest = MockData.getCryptoRequest();
        var platform = MockData.getPlatform("LEDGER");
        var crypto = MockData.getCrypto(platform.getId());

        when(platformRepositoryMock.findByName(cryptoRequest.platform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findByNameAndPlatformId(cryptoRequest.coin_name(), platform.getId()))
                .thenReturn(Optional.of(crypto));

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(cryptoRequest)
        );

        var message = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

        assertEquals(message, apiValidationException.getErrorMessage());
    }
}