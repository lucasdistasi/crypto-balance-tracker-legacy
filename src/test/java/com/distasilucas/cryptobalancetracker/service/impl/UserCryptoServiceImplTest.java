package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.UserCryptoService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
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
class UserCryptoServiceImplTest {

    @Mock
    UtilValidations utilValidationsMock;

    @Mock
    CryptoServiceImpl cryptoServiceMock;

    @Mock
    EntityMapper<UserCrypto, AddCryptoRequest> cryptoMapperImplMock;

    @Mock
    EntityMapper<UserCryptoResponse, UserCrypto> cryptoResponseMapperImplMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    UserCryptoRepository userCryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepository;

    @Mock
    Validation<AddCryptoRequest> addCryptoValidationMock;

    @Mock
    Validation<UpdateCryptoRequest> updateCryptoValidationMock;

    UserCryptoService userCryptoService;

    @BeforeEach
    void setUp() {
        userCryptoService = new UserCryptoServiceImpl(utilValidationsMock, cryptoServiceMock, cryptoMapperImplMock,
                cryptoResponseMapperImplMock, cryptoRepositoryMock, userCryptoRepositoryMock, platformRepository,
                addCryptoValidationMock, updateCryptoValidationMock);
    }

    @Test
    void shouldReturnCrypto() {
        var userCrypto = MockData.getUserCrypto();
        var crypto = Crypto.builder()
                .name("Bitcoin")
                .build();
        var platform = MockData.getPlatform();

        when(userCryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.of(userCrypto));
        when(platformRepository.findById("1234")).thenReturn(Optional.of(platform));
        when(cryptoRepositoryMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var cryptoResponse = userCryptoService.getCrypto(userCrypto.getId());

        assertAll(
                () -> assertEquals("Bitcoin", cryptoResponse.getCryptoName()),
                () -> assertEquals(userCrypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertEquals(platform.getName(), cryptoResponse.getPlatform()),
                () -> assertEquals(userCrypto.getId(), cryptoResponse.getId())
        );
    }

    @Test
    void shouldReturnCryptoWithUnknownPlatform() {
        var userCrypto = MockData.getUserCrypto("1234");
        var crypto = Crypto.builder()
                .name("Bitcoin")
                .build();

        when(userCryptoRepositoryMock.findById("1234")).thenReturn(Optional.of(userCrypto));
        when(platformRepository.findById("1234")).thenReturn(Optional.empty());
        when(cryptoRepositoryMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var cryptoResponse = userCryptoService.getCrypto("1234");

        assertAll(
                () -> assertEquals("Bitcoin", cryptoResponse.getCryptoName()),
                () -> assertEquals(userCrypto.getQuantity(), cryptoResponse.getQuantity()),
                () -> assertEquals(UNKNOWN, cryptoResponse.getPlatform()),
                () -> assertEquals(userCrypto.getId(), cryptoResponse.getId())
        );
    }

    @Test
    void shouldThrowCryptoNotFoundExceptionWhenRetrievingNonExistentCrypto() {
        when(userCryptoRepositoryMock.findById("1234")).thenReturn(Optional.empty());

        var cryptoNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> userCryptoService.getCrypto("1234"));

        assertAll(
                () -> assertEquals(String.format(CRYPTO_ID_NOT_FOUND, "1234"), cryptoNotFoundException.getErrorMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, cryptoNotFoundException.getHttpStatusCode())
        );
    }

    @Test
    void shouldRetrieveCryptosPage() {
        var crypto = UserCrypto.builder()
                .quantity(BigDecimal.valueOf(1))
                .build();
        var platform = MockData.getPlatform("Ledger");
        var cryptoResponse = MockData.getCryptoResponse();
        var page = 0;
        var pageable = PageRequest.of(page, 10);

        when(userCryptoRepositoryMock.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(crypto)));
        when(cryptoResponseMapperImplMock.mapFrom(crypto)).thenReturn(cryptoResponse);

        var cryptos = userCryptoService.getCryptos(page);

        assertAll(
                () -> assertTrue(cryptos.isPresent()),
                () -> assertNotNull(cryptos.get()),
                () -> assertNotEquals(0, cryptos.get().getCryptos().size()),
                () -> assertEquals(platform.getName(), cryptos.get().getCryptos().get(0).getPlatform()),
                () -> assertEquals(crypto.getQuantity(), cryptos.get().getCryptos().get(0).getQuantity()),
                () -> assertEquals(platform.getName(), cryptos.get().getCryptos().get(0).getPlatform())
        );
    }

    @Test
    void shouldReturnEmptyIfCryptosIsEmpty() {
        var page = 0;
        var pageable = PageRequest.of(page, 10);

        when(userCryptoRepositoryMock.findAll(pageable)).thenReturn(Page.empty());

        var cryptos = userCryptoService.getCryptos(page);

        assertAll(
                () -> assertEquals(Optional.empty(), cryptos),
                () -> assertTrue(cryptos.isEmpty())
        );
    }

    @Test
    void shouldAddCrypto() {
        var cryptoRequest = new AddCryptoRequest("Bitcoin", BigDecimal.valueOf(0.2), "Ledger");
        var cryptoEntity = UserCrypto.builder()
                .build();
        var cryptoResponse = UserCryptoResponse.builder()
                .cryptoName(cryptoRequest.getCryptoName())
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoRequest);
        when(cryptoMapperImplMock.mapFrom(cryptoRequest)).thenReturn(cryptoEntity);
        when(cryptoResponseMapperImplMock.mapFrom(cryptoEntity)).thenReturn(cryptoResponse);

        var actualCrypto = userCryptoService.saveUserCrypto(cryptoRequest);

        assertAll(
                () -> assertEquals(cryptoRequest.getCryptoName(), actualCrypto.getCryptoName()),
                () -> verify(addCryptoValidationMock, times(1)).validate(cryptoRequest),
                () -> verify(userCryptoRepositoryMock, times(1)).save(cryptoEntity),
                () -> verify(cryptoMapperImplMock, times(1)).mapFrom(cryptoRequest),
                () -> verify(cryptoResponseMapperImplMock, times(1)).mapFrom(cryptoEntity)
        );
    }

    @Test
    void shouldUpdateCrypto() {
        var newCryptoRequest = new UpdateCryptoRequest("Bitcoin", BigDecimal.valueOf(0.15), "BINANCE");
        var platform = Platform.builder()
                .id("321")
                .build();
        var existingCrypto = UserCrypto.builder()
                .id("ABC123")
                .build();
        var newCryptoResponse = UserCryptoResponse.builder()
                .quantity(newCryptoRequest.getQuantity())
                .platform(newCryptoRequest.getPlatform())
                .build();

        doNothing().when(updateCryptoValidationMock).validate(newCryptoRequest);
        when(userCryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));
        when(platformRepository.findByName(newCryptoRequest.getPlatform())).thenReturn(Optional.of(platform));
        when(cryptoResponseMapperImplMock.mapFrom(existingCrypto)).thenReturn(newCryptoResponse);

        var cryptoResponse = userCryptoService.updateCrypto(newCryptoRequest, "ABC123");

        assertAll(
                () -> verify(updateCryptoValidationMock, times(1)).validate(newCryptoRequest),
                () -> verify(userCryptoRepositoryMock, times(1)).findById("ABC123"),
                () -> verify(userCryptoRepositoryMock, times(1)).save(existingCrypto),
                () -> assertEquals(newCryptoRequest.getQuantity(), cryptoResponse.getQuantity())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentCrypto() {
        var cryptoRequest = new UpdateCryptoRequest("Bitcoin", BigDecimal.valueOf(0.15), "Ledger");

        doNothing().when(updateCryptoValidationMock).validate(cryptoRequest);
        when(userCryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.empty());

        var cryptoNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> userCryptoService.updateCrypto(cryptoRequest, "ABC123"));

        assertAll(
                () -> assertEquals(CRYPTO_NOT_FOUND, cryptoNotFoundException.getErrorMessage())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdateCryptoWithNonExistentPlatform() {
        var newCryptoRequest = new UpdateCryptoRequest("Bitcoin", BigDecimal.valueOf(0.15), "BINANCE");
        var existingCrypto = UserCrypto.builder()
                .id("ABC123")
                .build();

        doNothing().when(updateCryptoValidationMock).validate(newCryptoRequest);
        when(userCryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));
        when(platformRepository.findByName(newCryptoRequest.getPlatform())).thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> userCryptoService.updateCrypto(newCryptoRequest, "ABC123"));

        var message = String.format(PLATFORM_NOT_FOUND, newCryptoRequest.getPlatform());

        assertAll(
                () -> assertEquals(message, platformNotFoundException.getErrorMessage())
        );
    }

    @Test
    void shouldDeleteIcon() {
        var existingCrypto = UserCrypto.builder()
                .id("ABC123")
                .build();

        when(userCryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.of(existingCrypto));

        userCryptoService.deleteCrypto("ABC123");

        verify(userCryptoRepositoryMock, times(1)).delete(existingCrypto);
    }

    @Test
    void shouldThrowExceptionWhenDeleteNonExistentCrypto() {
        when(userCryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.empty());

        var cryptoNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> userCryptoService.deleteCrypto("ABC123"));

        assertAll(
                () -> assertEquals(CRYPTO_NOT_FOUND, cryptoNotFoundException.getErrorMessage())
        );
    }
}