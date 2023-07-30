package com.distasilucas.cryptobalancetracker.validation.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM;

@Service
public class PlatformNotExistsValidator implements EntityValidation<PlatformRequest> {

    private final PlatformService platformService;

    public PlatformNotExistsValidator(@Lazy PlatformService platformService) {
        this.platformService = platformService;
    }

    @Override
    public void validate(PlatformRequest platformRequest) {
        Optional<Platform> platform = platformService.findByName(platformRequest.getName());

        if (platform.isPresent()) {
            String message = String.format(DUPLICATED_PLATFORM, platformRequest.getName());

            throw new ApiValidationException(message);
        }
    }
}
