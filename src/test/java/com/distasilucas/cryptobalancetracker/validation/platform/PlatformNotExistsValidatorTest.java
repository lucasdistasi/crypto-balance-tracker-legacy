package com.distasilucas.cryptobalancetracker.validation.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.DUPLICATED_PLATFORM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformNotExistsValidatorTest {

    @Mock
    PlatformRepository platformRepositoryMock;

    EntityValidation<PlatformDTO> entityValidation;

    @BeforeEach
    void setUp() {
        entityValidation = new PlatformNotExistsValidator(platformRepositoryMock);
    }

    @Test
    void shouldValidateSuccessfully() {
        var platformDTO = new PlatformDTO();
        platformDTO.setName("TREZOR");

        when(platformRepositoryMock.findByName(platformDTO.getName())).thenReturn(Optional.empty());

        entityValidation.validate(platformDTO);
    }

    @Test
    void shouldThrowApiValidationExceptionWhenDuplicatedPlatformName() {
        var platformDTO = new PlatformDTO();
        platformDTO.setName("TREZOR");

        var platform = Platform.builder()
                .name(platformDTO.getName())
                .build();

        when(platformRepositoryMock.findByName(platformDTO.getName())).thenReturn(Optional.of(platform));

        ApiValidationException apiValidationException = assertThrows(ApiValidationException.class,
                () -> entityValidation.validate(platformDTO)
        );
        String message = String.format(DUPLICATED_PLATFORM, platformDTO.getName());


        assertEquals(message, apiValidationException.getErrorMessage());
    }

}