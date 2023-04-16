package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;
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
    EntityMapper<Crypto, CryptoRequest> cryptoMapperImplMock;

    @Mock
    EntityMapper<CryptoResponse, Crypto> cryptoResponseMapperImplMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepository;

    @Mock
    Validation<CryptoRequest> addCryptoValidationMock;

    @Mock
    Validation<CryptoRequest> updateCryptoValidationMock;

    CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoServiceImpl(cryptoMapperImplMock, cryptoResponseMapperImplMock, cryptoRepositoryMock,
                platformRepository, addCryptoValidationMock, updateCryptoValidationMock);
    }

    @Test
    void shouldReturnCoin() {
        var crypto = MockData.getCrypto("1234");
        var platform = MockData.getPlatform(crypto.getName());

        when(cryptoRepositoryMock.findById("1234")).thenReturn(Optional.of(crypto));
        when(platformRepository.findById("1234")).thenReturn(Optional.of(platform));

        var cryptoResponse = cryptoService.getCoin("1234");

        assertAll(
                () -> assertEquals("Bitcoin", cryptoResponse.getCoinName()),
                () -> assertEquals(crypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertEquals(platform.getName(), cryptoResponse.getPlatform()),
                () -> assertEquals(crypto.getId(), cryptoResponse.getCoinId())
        );
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenRetrievingNonExistentCoin() {
        when(cryptoRepositoryMock.findById("1234")).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> cryptoService.getCoin("1234"));

        assertAll(
                () -> assertEquals(String.format(COIN_ID_NOT_FOUND, "1234"), coinNotFoundException.getErrorMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, coinNotFoundException.getHttpStatusCode())
        );
    }

    @Test
    void shouldRetrieveAllCoins() {
        var crypto = Crypto.builder()
                .name("Ethereum")
                .quantity(BigDecimal.valueOf(1))
                .build();
        var platform = MockData.getPlatform("Ledger");
        var cryptoResponse = MockData.getCryptoResponse();

        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.singletonList(crypto));
        when(cryptoResponseMapperImplMock.mapFrom(crypto)).thenReturn(cryptoResponse);

        var coins = cryptoService.getCoins();

        assertAll(
                () -> assertTrue(coins.isPresent()),
                () -> assertNotNull(coins.get()),
                () -> assertNotEquals(0, coins.get().size()),
                () -> assertEquals(platform.getName(), coins.get().get(0).getPlatform()),
                () -> assertEquals(crypto.getQuantity(), coins.get().get(0).getQuantity()),
                () -> assertEquals(crypto.getName(), coins.get().get(0).getCoinName()),
                () -> assertEquals(platform.getName(), coins.get().get(0).getPlatform())
        );
    }

    @Test
    void shouldAddCrypto() {
        var cryptoRequest = new CryptoRequest("Bitcoin", BigDecimal.valueOf(0.2), "Ledger");
        var cryptoEntity = Crypto.builder()
                .name("Bitcoin")
                .build();
        var cryptoResponse = CryptoResponse.builder()
                .coinName(cryptoRequest.coin_name())
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoRequest);
        when(cryptoMapperImplMock.mapFrom(cryptoRequest)).thenReturn(cryptoEntity);
        when(cryptoResponseMapperImplMock.mapFrom(cryptoEntity)).thenReturn(cryptoResponse);

        var actualCrypto = cryptoService.addCoin(cryptoRequest);

        assertAll(
                () -> assertEquals(cryptoRequest.coin_name(), actualCrypto.getCoinName()),
                () -> verify(addCryptoValidationMock, times(1)).validate(cryptoRequest),
                () -> verify(cryptoRepositoryMock, times(1)).save(cryptoEntity),
                () -> verify(cryptoMapperImplMock, times(1)).mapFrom(cryptoRequest),
                () -> verify(cryptoResponseMapperImplMock, times(1)).mapFrom(cryptoEntity)
        );
    }

    @Test
    void shouldUpdateCoin() {
        var newCryptoRequest = new CryptoRequest("Bitcoin", BigDecimal.valueOf(0.15), "BINANCE");
        var platform = Platform.builder()
                .id("321")
                .build();
        var existingCrypto = Crypto.builder()
                .id("ABC123")
                .build();
        var newCryptoResponse = CryptoResponse.builder()
                .quantity(newCryptoRequest.quantity())
                .platform(newCryptoRequest.platform())
                .build();

        doNothing().when(updateCryptoValidationMock).validate(newCryptoRequest);
        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));
        when(platformRepository.findByName(newCryptoRequest.platform())).thenReturn(Optional.of(platform));
        when(cryptoResponseMapperImplMock.mapFrom(existingCrypto)).thenReturn(newCryptoResponse);

        var cryptoResponse = cryptoService.updateCoin(newCryptoRequest, "ABC123");

        assertAll(
                () -> verify(updateCryptoValidationMock, times(1)).validate(newCryptoRequest),
                () -> verify(cryptoRepositoryMock, times(1)).findById("ABC123"),
                () -> verify(cryptoRepositoryMock, times(1)).save(existingCrypto),
                () -> assertEquals(newCryptoRequest.quantity(), cryptoResponse.getQuantity())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentCrypto() {
        var cryptoRequest = new CryptoRequest("Bitcoin", BigDecimal.valueOf(0.15), "Ledger");

        doNothing().when(updateCryptoValidationMock).validate(cryptoRequest);
        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> cryptoService.updateCoin(cryptoRequest, "ABC123"));

        assertAll(
                () -> assertEquals("Coin not found", coinNotFoundException.getErrorMessage())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdateCryptoWithNonExistentPlatform() {
        var newCryptoRequest = new CryptoRequest("Bitcoin", BigDecimal.valueOf(0.15), "BINANCE");
        var existingCrypto = Crypto.builder()
                .id("ABC123")
                .build();

        doNothing().when(updateCryptoValidationMock).validate(newCryptoRequest);
        when(cryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));
        when(platformRepository.findByName(newCryptoRequest.platform())).thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> cryptoService.updateCoin(newCryptoRequest, "ABC123"));

        var message = String.format(PLATFORM_NOT_FOUND, newCryptoRequest.platform());

        assertAll(
                () -> assertEquals(message, platformNotFoundException.getErrorMessage())
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