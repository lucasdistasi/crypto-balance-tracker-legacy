package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND_DESCRIPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoPlatformValidatorTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    EntityValidation<CryptoDTO> entityValidation;

    @BeforeEach
    void setUp() {
        entityValidation = new CryptoPlatformValidator(cryptoRepositoryMock, platformRepositoryMock);
    }

    @Test
    void shouldThrowPlatformNotFoundExceptionWhenValidating() {
        var cryptoDTO = MockData.getCryptoDTO();

        when(platformRepositoryMock.findByName(cryptoDTO.platform().toUpperCase()))
                .thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> entityValidation.validate(cryptoDTO)
        );

        assertEquals(PLATFORM_NOT_FOUND_DESCRIPTION, platformNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowApiValidationExceptionWhenValidating() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platform = MockData.getPlatform("LEDGER");
        var crypto = MockData.getCrypto(platform.getId());

        when(platformRepositoryMock.findByName(cryptoDTO.platform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findByNameAndPlatformId(cryptoDTO.coin_name(), platform.getId()))
                .thenReturn(Optional.of(crypto));

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(cryptoDTO)
        );

        var message = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

        assertEquals(message, apiValidationException.getErrorMessage());
    }

    @Test
    void shouldValidateSuccessfully() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platform = MockData.getPlatform("LEDGER");

        when(platformRepositoryMock.findByName(cryptoDTO.platform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findByNameAndPlatformId(cryptoDTO.coin_name(), platform.getId()))
                .thenReturn(Optional.empty());

        entityValidation.validate(cryptoDTO);
    }
}