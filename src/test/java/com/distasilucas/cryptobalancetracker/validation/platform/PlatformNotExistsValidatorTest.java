package com.distasilucas.cryptobalancetracker.validation.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformNotExistsValidatorTest {

    @Mock
    PlatformService platformServiceMock;

    EntityValidation<PlatformRequest> entityValidation;

    @BeforeEach
    void setUp() {
        entityValidation = new PlatformNotExistsValidator(platformServiceMock);
    }

    @Test
    void shouldValidateSuccessfully() {
        var platformRequest = new PlatformRequest("TREZOR");

        when(platformServiceMock.findByName(platformRequest.getName())).thenReturn(Optional.empty());

        entityValidation.validate(platformRequest);
    }

    @Test
    void shouldThrowApiValidationExceptionWhenDuplicatedPlatformName() {
        var platformRequest = new PlatformRequest("TREZOR");

        var platform = Platform.builder()
                .name(platformRequest.getName())
                .build();

        when(platformServiceMock.findByName(platformRequest.getName())).thenReturn(Optional.of(platform));

        var apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(platformRequest)
        );

        var message = String.format(DUPLICATED_PLATFORM, platformRequest.getName());

        assertEquals(message, apiValidationException.getErrorMessage());
    }

}