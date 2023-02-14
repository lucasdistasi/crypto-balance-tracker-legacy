package com.distasilucas.cryptobalancetracker.validation.platform;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.DUPLICATED_PLATFORM;

@Service
@RequiredArgsConstructor
public class PlatformNotExistsValidator implements EntityValidation<PlatformDTO> {

    private final PlatformRepository platformRepository;

    @Override
    public void validate(PlatformDTO platformDTO) {
        Optional<Platform> platform = platformRepository.findByName(platformDTO.getName());

        if (platform.isPresent()) {
            String message = String.format(DUPLICATED_PLATFORM, platformDTO.getName());

            throw new ApiValidationException(message);
        }
    }
}
