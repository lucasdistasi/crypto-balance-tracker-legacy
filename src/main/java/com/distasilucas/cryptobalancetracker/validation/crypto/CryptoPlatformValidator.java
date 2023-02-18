package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND_DESCRIPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoPlatformValidator implements EntityValidation<CryptoDTO> {

    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;

    @Override
    public void validate(CryptoDTO cryptoDTO) {
        String platformName = cryptoDTO.platform().toUpperCase();
        Optional<Platform> optionalPlatform = platformRepository.findByName(platformName);

        if (optionalPlatform.isEmpty()) {
            log.info("Platform with id {} does not exists", cryptoDTO.platform());

            throw new PlatformNotFoundException(PLATFORM_NOT_FOUND_DESCRIPTION);
        }

        Platform platform = optionalPlatform.get();
        Optional<Crypto> optionalCrypto = cryptoRepository.findByNameAndPlatformId(cryptoDTO.coin_name(), platform.getId());

        if (optionalCrypto.isPresent()) {
            Crypto crypto = optionalCrypto.get();

            if (platform.getName().equalsIgnoreCase(cryptoDTO.platform())) {
                String message = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

                throw new ApiValidationException(message);
            }
        }
    }
}
