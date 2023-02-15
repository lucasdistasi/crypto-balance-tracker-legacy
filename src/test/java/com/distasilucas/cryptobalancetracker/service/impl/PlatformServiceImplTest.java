package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
    Validation<PlatformDTO> addPlatformValidationMock;

    @Mock
    EntityMapper<Platform, PlatformDTO> platformMapperImplMock;

    @Mock
    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImplMock;

    PlatformService platformService;

    @BeforeEach
    void setUp() {
        platformService = new PlatformServiceImpl(platformRepositoryMock, cryptoRepositoryMock,
                addPlatformValidationMock, platformMapperImplMock, cryptoBalanceResponseMapperImplMock);
    }

    @Test
    void shouldAddPlatform() {
        var platformDTO = new PlatformDTO("Trezor");

        var platform = Platform.builder()
                .name("Trezor")
                .build();

        doNothing().when(addPlatformValidationMock).validate(platformDTO);
        when(platformMapperImplMock.mapFrom(platformDTO)).thenReturn(platform);

        var platFormResponse = platformService.addPlatForm(platformDTO);

        assertAll(
                () -> verify(platformRepositoryMock, times(1)).save(platform),
                () -> assertEquals(platformDTO.getName(), platFormResponse.getName())
        );

    }

    @Test
    void shouldFindPlatform() {
        var platformName = "LEDGER";
        var platform = Platform.builder()
                .name(platformName)
                .build();

        when(platformRepositoryMock.findByName(platformName)).thenReturn(Optional.of(platform));

        var platformEntity = platformService.findPlatform(platformName);

        assertEquals(platformName, platformEntity.getName());
    }

    @Test
    void shouldThrowExceptionWhenPlatformNotExists() {
        var platformName = "LEDGER";
        var message = String.format(PLATFORM_NOT_FOUND, platformName.toUpperCase());

        when(platformRepositoryMock.findByName(platformName)).thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class, () -> platformService.findPlatform(platformName));

        assertEquals(message, platformNotFoundException.getErrorMessage());
    }

    @Test
    void shouldUpdatePlatform() {
        var platformDTO = new PlatformDTO("LEDGER");
        var platformEntity = Platform.builder()
                .name("TREZOR")
                .build();

        when(platformRepositoryMock.findByName("TREZOR")).thenReturn(Optional.of(platformEntity));

        var platform = platformService.updatePlatform(platformDTO, "Trezor");

        assertAll(
                () -> verify(platformRepositoryMock, times(1)).save(any()),
                () -> assertEquals(platformDTO.getName(), platform.getName())
        );
    }

    @Test
    void shouldDeletePlatform() {
        var platformEntity = Platform.builder()
                .name("LEDGER")
                .build();
        var allCryptos = getAllCryptos();
        var cryptoIds = allCryptos.stream()
                .map(Crypto::getName)
                .toList();

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platformEntity));
        when(cryptoRepositoryMock.findAllByPlatform(platformEntity)).thenReturn(Optional.of(allCryptos));

        platformService.deletePlatform("Ledger");

        assertAll(
                () -> verify(cryptoRepositoryMock, times(1)).deleteAllById(cryptoIds),
                () -> verify(platformRepositoryMock, times(1)).delete(platformEntity)
        );
    }

    @Test
    void shouldRetrieveAllCoinsForPlatform() {
        var platformEntity = Platform.builder()
                .name("LEDGER")
                .build();
        var allCryptos = getAllCryptos();
        var balanceResponse = getCryptoBalanceResponse();

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platformEntity));
        when(cryptoRepositoryMock.findAllByPlatform(platformEntity)).thenReturn(Optional.of(allCryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(allCryptos)).thenReturn(balanceResponse);

        var cryptoBalanceResponse = platformService.getAllCoins("Ledger");

        assertAll(
                () -> assertNotNull(cryptoBalanceResponse),
                () -> assertTrue(cryptoBalanceResponse.isPresent()),
                () -> assertEquals(balanceResponse.getTotalBalance(), cryptoBalanceResponse.get().getTotalBalance()),
                () -> assertEquals(1, cryptoBalanceResponse.get().getCoins().size()),
                () -> assertEquals(platformEntity.getName(), cryptoBalanceResponse.get().getCoins().get(0).getPlatform())
        );
    }

    private List<Crypto> getAllCryptos() {
        var platform = Platform.builder()
                .name("LEDGER")
                .build();

        return Collections.singletonList(
                Crypto.builder()
                        .platform(platform)
                        .build()
        );
    }

    private CryptoBalanceResponse getCryptoBalanceResponse() {
        var coinInfo = new CoinInfo();
        coinInfo.setSymbol("BTC");

        var coinResponse = new CoinResponse(coinInfo, BigDecimal.valueOf(5), BigDecimal.valueOf(1000), "LEDGER");

        var cryptoBalanceResponse = new CryptoBalanceResponse();
        cryptoBalanceResponse.setTotalBalance(BigDecimal.valueOf(1000));
        cryptoBalanceResponse.setCoins(Collections.singletonList(coinResponse));

        return cryptoBalanceResponse;
    }

}