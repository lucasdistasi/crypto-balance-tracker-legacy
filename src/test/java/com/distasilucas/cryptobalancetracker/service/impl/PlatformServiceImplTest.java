package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformServiceImplTest {

    @Mock
    PlatformRepository platformRepositoryMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    Validation<PlatformRequest> addPlatformValidationMock;

    @Mock
    EntityMapper<Platform, PlatformRequest> platformMapperImplMock;

    @Mock
    EntityMapper<PlatformResponse, Platform> platformResponseMapperImplMock;

    PlatformService platformService;

    @BeforeEach
    void setUp() {
        platformService = new PlatformServiceImpl(platformRepositoryMock, cryptoRepositoryMock, addPlatformValidationMock,
                platformMapperImplMock, platformResponseMapperImplMock);
    }

    @Test
    void shouldGetAllPlatforms() {
        var platform = Platform.builder()
                .id("1234")
                .name("Binance")
                .build();

        when(platformRepositoryMock.findAll()).thenReturn(Collections.singletonList(platform));

        var allPlatforms = platformService.getAllPlatforms();

        assertAll(
                () -> assertFalse(allPlatforms.isEmpty()),
                () -> assertEquals(1, allPlatforms.size())
        );
    }

    @Test
    void shouldAddPlatform() {
        var platformRequest = new PlatformRequest("Trezor");

        var platform = Platform.builder()
                .name("Trezor")
                .build();

        doNothing().when(addPlatformValidationMock).validate(platformRequest);
        when(platformMapperImplMock.mapFrom(platformRequest)).thenReturn(platform);

        var platFormResponse = platformService.addPlatForm(platformRequest);

        assertAll(
                () -> verify(platformRepositoryMock, times(1)).save(platform),
                () -> assertEquals(platformRequest.getName(), platFormResponse.getName())
        );
    }

    @Test
    void shouldFindPlatform() {
        var platformName = "LEDGER";
        var platform = Platform.builder()
                .name(platformName)
                .build();

        when(platformRepositoryMock.findByName(platformName)).thenReturn(Optional.of(platform));

        var platformEntity = platformService.findPlatformByName(platformName);

        assertEquals(platformName, platformEntity.getName());
    }

    @Test
    void shouldThrowExceptionWhenPlatformNotExists() {
        var platformName = "LEDGER";
        var message = String.format(PLATFORM_NOT_FOUND, platformName.toUpperCase());

        when(platformRepositoryMock.findByName(platformName)).thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class, () -> platformService.findPlatformByName(platformName));

        assertEquals(message, platformNotFoundException.getErrorMessage());
    }

    @Test
    void shouldUpdatePlatform() {
        var platformRequest = MockData.getPlatformRequest("Ledger");
        var platformEntity = MockData.getPlatform("TREZOR");

        when(platformRepositoryMock.findByName("TREZOR")).thenReturn(Optional.of(platformEntity));

        var platformResponse = platformService.updatePlatform("Trezor", platformRequest);

        assertAll(
                () -> verify(platformRepositoryMock, times(1)).save(any()),
                () -> assertEquals(platformRequest.getName(), platformResponse.getName())
        );
    }

    @Test
    void shouldDeletePlatform() {
        var platformEntity = MockData.getPlatform("Ledger");
        var allCryptos = MockData.getAllCryptos();
        var cryptoIds = allCryptos.stream()
                .collect(Collectors.toMap(Crypto::getId, Crypto::getName));

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platformEntity));
        when(cryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.of(allCryptos));

        platformService.deletePlatform("Ledger");

        assertAll(
                () -> verify(cryptoRepositoryMock, times(1)).deleteAllById(cryptoIds.keySet()),
                () -> verify(platformRepositoryMock, times(1)).delete(platformEntity)
        );
    }

    @Test
    void shouldNotCallDeleteCryptosIfNoCryptosFoundForPlatform() {
        var platformEntity = MockData.getPlatform("Ledger");

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platformEntity));
        when(cryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.empty());

        platformService.deletePlatform("Ledger");

        assertAll(
                () -> verify(cryptoRepositoryMock, never()).deleteAllById(any()),
                () -> verify(platformRepositoryMock, times(1)).delete(platformEntity)
        );
    }
}