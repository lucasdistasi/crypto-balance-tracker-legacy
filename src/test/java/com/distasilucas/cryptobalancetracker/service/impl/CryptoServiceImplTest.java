package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND_DESCRIPTION;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoServiceImplTest {

    @Mock
    EntityMapper<Crypto, CryptoDTO> cryptoMapperImplMock;

    @Mock
    EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImplMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepository;

    @Mock
    Validation<CryptoDTO> addCryptoValidationMock;

    @Mock
    Validation<CryptoDTO> updateCryptoValidationMock;

    CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoServiceImpl(cryptoMapperImplMock, cryptoDTOMapperImplMock, cryptoRepositoryMock,
                platformRepository, addCryptoValidationMock, updateCryptoValidationMock);
    }

    @Test
    void shouldReturnCoin() {
        var crypto = MockData.getCrypto("1234");
        var platform = MockData.getPlatform(crypto.getName());

        when(cryptoRepositoryMock.findById("1234")).thenReturn(Optional.of(crypto));
        when(platformRepository.findById("1234")).thenReturn(Optional.of(platform));

        var cryptoDTO = cryptoService.getCoin("1234");

        assertAll(
                () -> assertTrue(cryptoDTO.isPresent()),
                () -> assertNotNull(cryptoDTO.get()),
                () -> assertEquals("Bitcoin", cryptoDTO.get().coin_name()),
                () -> assertEquals(crypto.getQuantity(), cryptoDTO.get().quantity()),
                () -> assertEquals(platform.getName(), cryptoDTO.get().platform())
        );
    }

    @Test
    void shouldReturnEmptyIfCoinNotExists() {
        when(cryptoRepositoryMock.findById("1234")).thenReturn(Optional.empty());

        var cryptoDTO = cryptoService.getCoin("1234");

        assertTrue(cryptoDTO.isEmpty());
    }

    @Test
    void shouldRetrieveAllCoins() {
        var crypto = MockData.getCrypto("1234");
        var platformId = crypto.getPlatformId();
        var platform = MockData.getPlatform("Binance");

        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.singletonList(crypto));
        when(platformRepository.findById(platformId)).thenReturn(Optional.of(platform));

        var coins = cryptoService.getCoins();

        assertAll(
                () -> assertTrue(coins.isPresent()),
                () -> assertNotNull(coins.get()),
                () -> assertNotEquals(0, coins.get().size()),
                () -> assertEquals(platform.getName(), coins.get().get(0).platform()),
                () -> assertEquals(crypto.getQuantity(), coins.get().get(0).quantity()),
                () -> assertEquals(crypto.getName(), coins.get().get(0).coin_name()),
                () -> assertEquals(platform.getName(), coins.get().get(0).platform())
        );
    }

    @Test
    void shouldAddCrypto() {
        var cryptoDTO = CryptoDTO.builder()
                .coin_name("Bitcoin")
                .build();
        var cryptoEntity = Crypto.builder()
                .name("Bitcoin")
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoDTO);
        when(cryptoMapperImplMock.mapFrom(cryptoDTO)).thenReturn(cryptoEntity);
        when(cryptoDTOMapperImplMock.mapFrom(cryptoEntity)).thenReturn(cryptoDTO);

        var actualCrypto = cryptoService.addCoin(cryptoDTO);

        assertAll(
                () -> assertEquals(cryptoDTO.coin_name(), actualCrypto.coin_name()),
                () -> verify(addCryptoValidationMock, times(1)).validate(cryptoDTO),
                () -> verify(cryptoRepositoryMock, times(1)).save(cryptoEntity),
                () -> verify(cryptoMapperImplMock, times(1)).mapFrom(cryptoDTO),
                () -> verify(cryptoDTOMapperImplMock, times(1)).mapFrom(cryptoEntity)
        );
    }

    @Test
    void shouldUpdateCoin() {
        var newCryptoDTO = CryptoDTO.builder()
                .quantity(BigDecimal.valueOf(0.15))
                .platform("BINANCE")
                .build();
        var platform = Platform.builder()
                .id("321")
                .build();
        var existingCrypto = Crypto.builder()
                .id("ABC123")
                .build();

        doNothing().when(updateCryptoValidationMock).validate(newCryptoDTO);
        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));
        when(platformRepository.findByName(newCryptoDTO.platform())).thenReturn(Optional.of(platform));
        when(cryptoDTOMapperImplMock.mapFrom(existingCrypto)).thenReturn(newCryptoDTO);

        CryptoDTO cryptoDTO = cryptoService.updateCoin(newCryptoDTO, "ABC123");

        assertAll(
                () -> verify(updateCryptoValidationMock, times(1)).validate(newCryptoDTO),
                () -> verify(cryptoRepositoryMock, times(1)).findById("ABC123"),
                () -> verify(cryptoRepositoryMock, times(1)).save(existingCrypto),
                () -> assertEquals(newCryptoDTO.quantity(), cryptoDTO.quantity())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentCrypto() {
        var newCryptoDTO = CryptoDTO.builder()
                .quantity(BigDecimal.valueOf(0.15))
                .build();

        doNothing().when(updateCryptoValidationMock).validate(newCryptoDTO);
        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> cryptoService.updateCoin(newCryptoDTO, "ABC123"));

        assertAll(
                () -> assertEquals("Coin not found", coinNotFoundException.getErrorMessage())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdateCryptoWithNonExistentPlatform() {
        var newCryptoDTO = CryptoDTO.builder()
                .quantity(BigDecimal.valueOf(0.15))
                .platform("BINANCE")
                .build();
        var existingCrypto = Crypto.builder()
                .id("ABC123")
                .build();

        doNothing().when(updateCryptoValidationMock).validate(newCryptoDTO);
        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));
        when(platformRepository.findByName("BINANCE")).thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> cryptoService.updateCoin(newCryptoDTO, "ABC123"));

        assertAll(
                () -> assertEquals(PLATFORM_NOT_FOUND_DESCRIPTION, platformNotFoundException.getErrorMessage())
        );
    }

    @Test
    void shouldDeleteIcon() {
        var existingCrypto = Crypto.builder()
                .id("ABC123")
                .build();

        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));

        cryptoService.deleteCoin("ABC123");

        verify(cryptoRepositoryMock, times(1)).delete(existingCrypto);
    }

    @Test
    void shouldThrowExceptionWhenDeleteNonExistentCrypto() {
        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> cryptoService.deleteCoin("ABC123"));

        assertAll(
                () -> assertEquals("Coin not found", coinNotFoundException.getErrorMessage())
        );
    }
}