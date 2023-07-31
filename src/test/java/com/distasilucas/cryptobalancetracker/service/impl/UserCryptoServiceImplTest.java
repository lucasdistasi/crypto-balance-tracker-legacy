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
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
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
import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    CryptoService cryptoServiceMock;

    @Mock
    EntityMapper<UserCrypto, AddCryptoRequest> cryptoMapperImplMock;

    @Mock
    EntityMapper<UserCryptoResponse, UserCrypto> cryptoResponseMapperImplMock;

    @Mock
    com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository userCryptoRepositoryMock;

    @Mock
    PlatformService platformServiceMock;

    @Mock
    Validation<AddCryptoRequest> addCryptoValidationMock;

    @Mock
    Validation<UpdateCryptoRequest> updateCryptoValidationMock;

    UserCryptoService userCryptoService;

    @BeforeEach
    void setUp() {
        userCryptoService = new UserCryptoServiceImpl(utilValidationsMock, cryptoServiceMock, cryptoMapperImplMock,
                cryptoResponseMapperImplMock, userCryptoRepositoryMock, platformServiceMock, addCryptoValidationMock,
                updateCryptoValidationMock);
    }

    @Test
    void shouldFindAllByCryptoId() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .build();

        when(userCryptoRepositoryMock.findAllByCryptoId("bitcoin"))
                .thenReturn(Optional.of(Collections.singletonList(userCrypto)));

        var userCryptos = userCryptoService.findAllByCryptoId("bitcoin");

        assertTrue(userCryptos.isPresent());
    }

    @Test
    void shouldFindById() {
        var userCrypto = UserCrypto.builder()
                .id("ABC123")
                .cryptoId("bitcoin")
                .build();

        when(userCryptoRepositoryMock.findById("ABC123"))
                .thenReturn(Optional.of(userCrypto));

        var savedUserCrypto = userCryptoService.findById("ABC123");

        assertTrue(savedUserCrypto.isPresent());
    }

    @Test
    void shouldFindByCryptoIdAndPlatformId() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .platformId("ABC123")
                .build();

        when(userCryptoRepositoryMock.findByCryptoIdAndPlatformId("bitcoin", "ABC123"))
                .thenReturn(Optional.of(userCrypto));

        var savedUserCrypto = userCryptoService.findByCryptoIdAndPlatformId("bitcoin", "ABC123");

        assertTrue(savedUserCrypto.isPresent());
    }

    @Test
    void shouldFindAll() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .platformId("ABC123")
                .build();

        when(userCryptoRepositoryMock.findAll()).thenReturn(Collections.singletonList(userCrypto));

        var savedUserCrypto = userCryptoService.findAll();

        assertFalse(savedUserCrypto.isEmpty());
    }

    @Test
    void shouldFindAllByPlatformId() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .platformId("ABC123")
                .build();

        when(userCryptoRepositoryMock.findAllByPlatformId("ABC123"))
                .thenReturn(Optional.of(Collections.singletonList(userCrypto)));

        var savedUserCrypto = userCryptoService.findAllByPlatformId("ABC123");

        assertTrue(savedUserCrypto.isPresent());
    }

    @Test
    void shouldSaveUserCrypto() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .platformId("ABC123")
                .build();

        userCryptoService.saveUserCrypto(userCrypto);

        verify(userCryptoRepositoryMock, times(1)).save(userCrypto);
    }

    @Test
    void shouldSaveAllUserCrypto() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .platformId("ABC123")
                .build();

        userCryptoService.saveAll(Collections.singletonList(userCrypto));

        verify(userCryptoRepositoryMock, times(1)).saveAll(Collections.singletonList(userCrypto));
    }

    @Test
    void shouldReturnCrypto() {
        var userCrypto = MockData.getUserCrypto();
        var crypto = Crypto.builder()
                .name("Bitcoin")
                .build();
        var platform = MockData.getPlatform();

        when(userCryptoRepositoryMock.findById("ABC1234")).thenReturn(Optional.of(userCrypto));
        when(platformServiceMock.findById("1234")).thenReturn(Optional.of(platform));
        when(cryptoServiceMock.findById(userCrypto.getCryptoId())).thenReturn(Optional.of(crypto));

        var cryptoResponse = userCryptoService.getUserCryptoResponse(userCrypto.getId());

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

        when(userCryptoRepositoryMock.findById("1234")).thenReturn(Optional.of(userCrypto));
        when(platformServiceMock.findById("1234")).thenReturn(Optional.empty());

        var exception = assertThrows(PlatformNotFoundException.class,
                () -> userCryptoService.getUserCryptoResponse("1234"));

        assertEquals(PLATFORM_NOT_FOUND, exception.getErrorMessage());
    }

    @Test
    void shouldThrowCryptoNotFoundExceptionWhenRetrievingNonExistentCrypto() {
        when(userCryptoRepositoryMock.findById("1234")).thenReturn(Optional.empty());

        var cryptoNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> userCryptoService.getUserCryptoResponse("1234"));

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
        when(platformServiceMock.findByName(newCryptoRequest.getPlatform())).thenReturn(Optional.of(platform));
        when(cryptoResponseMapperImplMock.mapFrom(existingCrypto)).thenReturn(newCryptoResponse);

        var cryptoResponse = userCryptoService.updateUserCrypto(newCryptoRequest, "ABC123");

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
                () -> userCryptoService.updateUserCrypto(cryptoRequest, "ABC123"));

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
        when(platformServiceMock.findByName(newCryptoRequest.getPlatform())).thenReturn(Optional.empty());

        var platformNotFoundException = assertThrows(PlatformNotFoundException.class,
                () -> userCryptoService.updateUserCrypto(newCryptoRequest, "ABC123"));

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

        userCryptoService.deleteUserCrypto("ABC123");

        verify(userCryptoRepositoryMock, times(1)).delete(existingCrypto);
    }

    @Test
    void shouldThrowExceptionWhenDeleteNonExistentCrypto() {
        when(userCryptoRepositoryMock.findById("ABC123")).thenReturn(Optional.empty());

        var cryptoNotFoundException = assertThrows(CryptoNotFoundException.class,
                () -> userCryptoService.deleteUserCrypto("ABC123"));

        assertAll(
                () -> assertEquals(CRYPTO_NOT_FOUND, cryptoNotFoundException.getErrorMessage())
        );
    }

    @Test
    void shouldDeleteCrypto() {
        var userCrypto = UserCrypto.builder()
                .cryptoId("bitcoin")
                .platformId("ABC123")
                .build();

        userCryptoService.deleteUserCrypto(userCrypto);

        verify(userCryptoRepositoryMock, times(1)).delete(userCrypto);
        verify(cryptoServiceMock, times(1)).deleteCryptoIfNotUsed("bitcoin");
    }
}