package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.DuplicatedPlatformCoinException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.Constants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.Constants.NO_COIN_IN_PLATFORM;
import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    Validation<CryptoDTO> updateCryptoValidationMock;

    @Mock
    EntityMapper<Platform, PlatformDTO> platformMapperImplMock;

    @Mock
    EntityMapper<PlatformDTO, Platform> platformDTOMapperImplMock;

    @Mock
    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImplMock;

    @Mock
    EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImplMock;

    PlatformService platformService;

    @BeforeEach
    void setUp() {
        platformService = new PlatformServiceImpl(platformRepositoryMock, cryptoRepositoryMock,
                addPlatformValidationMock, updateCryptoValidationMock, platformMapperImplMock,
                platformDTOMapperImplMock, cryptoBalanceResponseMapperImplMock, cryptoDTOMapperImplMock);
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
    void shouldGetPlatformsBalances() {
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(cryptoRepositoryMock.findAll()).thenReturn(cryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var platformsBalances = platformService.getPlatformsBalances();

        assertAll(
                () -> assertTrue(platformsBalances.isPresent()),
                () -> assertEquals(BigDecimal.valueOf(1000), platformsBalances.get().getTotalBalance()),
                () -> assertEquals(1, platformsBalances.get().getPlatforms().size())
        );
    }

    @Test
    void shouldEmptyGetPlatformsBalances() {
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        balanceResponse.setCoins(Collections.emptyList());

        when(cryptoRepositoryMock.findAll()).thenReturn(cryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var platformsBalances = platformService.getPlatformsBalances();

        assertAll(
                () -> assertTrue(platformsBalances.isEmpty())
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
        var platformDTO = MockData.getPlatformDTO("Ledger");
        var platformEntity = MockData.getPlatform("Trezor");

        when(platformRepositoryMock.findByName("TREZOR")).thenReturn(Optional.of(platformEntity));

        var platform = platformService.updatePlatform(platformDTO, "Trezor");

        assertAll(
                () -> verify(platformRepositoryMock, times(1)).save(any()),
                () -> assertEquals(platformDTO.getName(), platform.getName())
        );
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenUpdatePlatformCoin() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platform = MockData.getPlatform(cryptoDTO.platform());

        doNothing().when(updateCryptoValidationMock).validate(cryptoDTO);
        when(platformRepositoryMock.findByName(cryptoDTO.platform().toUpperCase()))
                .thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId("bitcoin", platform.getId()))
                .thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> platformService.updatePlatformCoin(cryptoDTO, "Ledger", "bitcoin"));

        var message = String.format(NO_COIN_IN_PLATFORM, "bitcoin", "Ledger");

        assertEquals(message, coinNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowDuplicatedPlatformCoinExceptionWhenUpdatePlatformCoin() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platform = MockData.getPlatform(cryptoDTO.platform());
        var newPlatform = MockData.getPlatform("Safepal");
        var cryptoEntity = MockData.getCrypto("1234");
        var newCryptoEntity = MockData.getCrypto("4321");

        doNothing().when(updateCryptoValidationMock).validate(cryptoDTO);
        when(platformRepositoryMock.findByName(cryptoDTO.platform().toUpperCase()))
                .thenReturn(Optional.of(newPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId("bitcoin", platform.getId()))
                .thenReturn(Optional.of(cryptoEntity));
        when(cryptoRepositoryMock.findByNameAndPlatformId(cryptoEntity.getName(), cryptoEntity.getPlatformId()))
                .thenReturn(Optional.of(newCryptoEntity));

        var duplicatedPlatformCoinException = assertThrows(DuplicatedPlatformCoinException.class,
                () -> platformService.updatePlatformCoin(cryptoDTO, "Ledger", "bitcoin"));

        var message = String.format(DUPLICATED_PLATFORM_COIN, newCryptoEntity.getCoinId(), newPlatform.getName());

        assertEquals(message, duplicatedPlatformCoinException.getErrorMessage());
    }

    @Test
    void shouldUpdatePlatformCoin() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platform = MockData.getPlatform(cryptoDTO.platform());
        var newPlatform = MockData.getPlatform("Safepal");
        var cryptoEntity = MockData.getCrypto("1234");

        doNothing().when(updateCryptoValidationMock).validate(cryptoDTO);
        when(platformRepositoryMock.findByName(cryptoDTO.platform().toUpperCase()))
                .thenReturn(Optional.of(newPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId("bitcoin", platform.getId()))
                .thenReturn(Optional.of(cryptoEntity));
        when(cryptoRepositoryMock.findByNameAndPlatformId(cryptoEntity.getName(), cryptoEntity.getPlatformId()))
                .thenReturn(Optional.empty());
        when(cryptoDTOMapperImplMock.mapFrom(cryptoEntity)).thenReturn(cryptoDTO);

        var updatePlatformCoin = platformService.updatePlatformCoin(cryptoDTO, "Ledger", "bitcoin");

        assertAll(
                () -> verify(cryptoRepositoryMock, times(1)).save(cryptoEntity),
                () -> assertEquals(cryptoDTO.platform(), updatePlatformCoin.platform()),
                () -> assertEquals(cryptoDTO.coinId(), updatePlatformCoin.coinId()),
                () -> assertEquals(cryptoDTO.coin_name(), updatePlatformCoin.coin_name()),
                () -> assertEquals(cryptoDTO.ticker(), updatePlatformCoin.ticker())
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
    void shouldThrowPlatformNotFoundExceptionWhenDeletePlatformCoin() {
        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> platformService.deletePlatformCoin("Ledger", "Bitcoin"));

        var message = String.format(PLATFORM_NOT_FOUND, "Ledger");

        assertEquals(message, platformNotFoundException.getErrorMessage());
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenDeletePlatformCoin() {
        var platformEntity = MockData.getPlatform("Ledger");

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platformEntity));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId("bitcoin", platformEntity.getId()))
                .thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> platformService.deletePlatformCoin("Ledger", "bitcoin"));

        var message = String.format(NO_COIN_IN_PLATFORM, "bitcoin", platformEntity.getName());

        assertEquals(message, coinNotFoundException.getErrorMessage());
    }

    @Test
    void shouldDeletePlatformCoin() {
        var platformEntity = MockData.getPlatform("Ledger");
        var cryptoEntity = MockData.getCrypto(platformEntity.getName());

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platformEntity));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId("bitcoin", platformEntity.getId()))
                .thenReturn(Optional.of(cryptoEntity));

        platformService.deletePlatformCoin("Ledger", "bitcoin");

        verify(cryptoRepositoryMock, times(1)).delete(cryptoEntity);
    }

    @Test
    void shouldRetrieveAllCoinsForPlatform() {
        var platformEntity = MockData.getPlatform("LEDGER");
        var allCryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();

        when(platformRepositoryMock.findByName("LEDGER")).thenReturn(Optional.of(platformEntity));
        when(cryptoRepositoryMock.findAllByPlatformId("1234")).thenReturn(Optional.of(allCryptos));
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
}