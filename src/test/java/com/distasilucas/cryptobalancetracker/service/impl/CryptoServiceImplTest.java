package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.BiFunctionMapper;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinInfoResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND_DESCRIPTION;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImplMock;

    @Mock
    BiFunctionMapper<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> coinInfoResponseMapperImplMock;

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
        cryptoService = new CryptoServiceImpl(cryptoMapperImplMock, cryptoDTOMapperImplMock, cryptoBalanceResponseMapperImplMock,
                coinInfoResponseMapperImplMock, cryptoRepositoryMock, platformRepository, addCryptoValidationMock, updateCryptoValidationMock);
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
                () -> assertTrue(cryptoBalanceResponse.isPresent()),
                () -> assertEquals(expectedBalance, cryptoBalanceResponse.get().getTotalBalance()),
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
                () -> assertTrue(cryptoBalanceResponses.isEmpty())
        );
    }

    @Test
    void shouldRetrieveBalancesForCrypto() {
        var coinInfo = MockData.getCoinInfo();
        var cryptos = MockData.getAllCryptos();
        var balanceResponse = MockData.getCryptoBalanceResponse();
        var firstCoin = balanceResponse.getCoins().get(0);

        when(cryptoRepositoryMock.findAllByCoinId("bitcoin")).thenReturn(Optional.of(cryptos));
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var cryptoBalanceResponse = cryptoService.retrieveCoinBalance("bitcoin");
        var expectedBalance = MockData.getTotalMoney(Collections.singletonList(firstCoin));

        assertAll(
                () -> assertTrue(cryptoBalanceResponse.isPresent()),
                () -> assertEquals(expectedBalance, cryptoBalanceResponse.get().getTotalBalance()),
                () -> assertEquals(BigDecimal.valueOf(5), firstCoin.getQuantity()),
                () -> assertEquals(100, firstCoin.getPercentage()),
                () -> assertEquals(coinInfo, firstCoin.getCoinInfo())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoCryptosAreSavedForCryptoBalance() {
        when(cryptoRepositoryMock.findAllByCoinId("dogecoin")).thenReturn(Optional.of(Collections.emptyList()));

        var cryptoBalanceResponses = cryptoService.retrieveCoinBalance("dogecoin");

        assertAll(
                () -> assertTrue(cryptoBalanceResponses.isEmpty())
        );
    }

    @Test
    void shouldRetrieveCoinsBalanceByPlatform() {
        var allCryptos = MockData.getAllCryptos();
        var cryptoBalanceResponse = MockData.getCryptoBalanceResponse();
        var coinInfoResponse = MockData.getCoinInfoResponse();
        BiFunction<Map<String, BigDecimal>, CryptoBalanceResponse, List<CoinInfoResponse>> biFunction = (a, b) -> Collections.singletonList(coinInfoResponse);

        when(cryptoRepositoryMock.findAll()).thenReturn(allCryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(allCryptos)).thenReturn(cryptoBalanceResponse);
        when(coinInfoResponseMapperImplMock.map()).thenReturn(biFunction);

        var platformBalanceResponse = cryptoService.retrieveCoinsBalanceByPlatform();

        assertAll(
                () -> assertTrue(platformBalanceResponse.isPresent()),
                () -> assertEquals(cryptoBalanceResponse.getTotalBalance(), platformBalanceResponse.get().getTotalBalance()),
                () -> assertTrue(CollectionUtils.isNotEmpty(platformBalanceResponse.get().getCoinInfoResponse())),
                () -> assertEquals(coinInfoResponse.getName(), platformBalanceResponse.get().getCoinInfoResponse().get(0).getName())
        );
    }

    @Test
    void shouldRetrieveEmptyCoinsBalanceByPlatform() {
        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var platformBalanceResponse = cryptoService.retrieveCoinsBalanceByPlatform();

        assertAll(
                () -> assertTrue(platformBalanceResponse.isEmpty())
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