package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
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

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImplMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    Validation<CryptoDTO> addCryptoValidationMock;

    @Mock
    Validation<CryptoDTO> updateCryptoValidationMock;

    CryptoService<CryptoDTO> cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoServiceImpl(cryptoMapperImplMock, cryptoDTOMapperImplMock, cryptoBalanceResponseMapperImplMock,
                cryptoRepositoryMock, addCryptoValidationMock, updateCryptoValidationMock);
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
    void shouldRetrieveCryptoBalances() {
        var coinInfo = MockData.getCoinInfo();
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCoin = balanceResponse.getCoins().get(0);

        when(cryptoRepositoryMock.findAll()).thenReturn(cryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var cryptoBalanceResponse = cryptoService.retrieveCoinsBalances();
        var expectedBalance = MockData.getTotalMoney(Collections.singletonList(firstCoin));

        assertAll(
                () -> assertEquals(expectedBalance, cryptoBalanceResponse.getTotalBalance()),
                () -> assertEquals(BigDecimal.valueOf(5), firstCoin.getQuantity()),
                () -> assertEquals(100, firstCoin.getPercentage()),
                () -> assertEquals(coinInfo, firstCoin.getCoinInfo())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoCryptosAreSaved() {
        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var cryptoBalanceResponses = cryptoService.retrieveCoinsBalances();

        assertAll(
                () -> assertNull(cryptoBalanceResponses)
        );
    }

    @Test
    void shouldUpdateCoin() {
        var cryptoDTO = CryptoDTO.builder()
                .coin_name("Bitcoin")
                .quantity(BigDecimal.valueOf(2))
                .build();
        var crypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(1))
                .build();

        doNothing().when(updateCryptoValidationMock).validate(cryptoDTO);
        when(cryptoRepositoryMock.findByName(cryptoDTO.coin_name())).thenReturn(Optional.of(crypto));
        when(cryptoDTOMapperImplMock.mapFrom(crypto)).thenReturn(cryptoDTO);

        var updatedCrypto = cryptoService.updateCoin(cryptoDTO, "Bitcoin");

        assertAll(
                () -> assertEquals(cryptoDTO.quantity(), updatedCrypto.quantity()),
                () -> verify(cryptoRepositoryMock, times(1)).save(crypto)
        );

    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenUpdatingNonExistentCoin() {
        var cryptoDTO = CryptoDTO.builder()
                .coin_name("Dogecoin")
                .build();

        doNothing().when(updateCryptoValidationMock).validate(cryptoDTO);
        when(cryptoRepositoryMock.findByName(cryptoDTO.coin_name())).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(
                CoinNotFoundException.class,
                () -> cryptoService.updateCoin(cryptoDTO, "Dogecoin")
        );

        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, cryptoDTO.coin_name());
        assertAll(
                () -> assertEquals(coinNotFoundException.getErrorMessage(), expectedMessage)
        );
    }

    @Test
    void shouldDeleteCoin() {
        var cryptoEntity = Crypto.builder()
                .name("Shiba")
                .build();

        when(cryptoRepositoryMock.findByName("Shiba")).thenReturn(Optional.of(cryptoEntity));

        cryptoService.deleteCoin("Shiba");

        verify(cryptoRepositoryMock, times(1)).delete(cryptoEntity);
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenDeleting() {
        when(cryptoRepositoryMock.findByName("Shiba")).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class, () -> cryptoService.deleteCoin("Shiba"));

        assertAll(
                () -> assertEquals(String.format(COIN_NAME_NOT_FOUND, "Shiba"), coinNotFoundException.getErrorMessage())
        );
    }
}