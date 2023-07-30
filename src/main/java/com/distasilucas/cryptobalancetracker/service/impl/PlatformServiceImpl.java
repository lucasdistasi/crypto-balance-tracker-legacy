package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.platform.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.UserCryptoService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
public class PlatformServiceImpl implements PlatformService {

    private final UtilValidations utilValidations;
    private final PlatformRepository platformRepository;
    private final UserCryptoService userCryptoService;
    private final Validation<PlatformRequest> addPlatformValidation;
    private final EntityMapper<Platform, PlatformRequest> platformMapperImpl;
    private final EntityMapper<PlatformResponse, Platform> platformResponseMapperImpl;

    public PlatformServiceImpl(UtilValidations utilValidations,
                               PlatformRepository platformRepository,
                               @Lazy UserCryptoService userCryptoService,
                               Validation<PlatformRequest> addPlatformValidation,
                               EntityMapper<Platform, PlatformRequest> platformMapperImpl,
                               EntityMapper<PlatformResponse, Platform> platformResponseMapperImpl) {
        this.utilValidations = utilValidations;
        this.platformRepository = platformRepository;
        this.userCryptoService = userCryptoService;
        this.addPlatformValidation = addPlatformValidation;
        this.platformMapperImpl = platformMapperImpl;
        this.platformResponseMapperImpl = platformResponseMapperImpl;
    }

    @Override
    public Optional<Platform> findById(String id) {
        return platformRepository.findById(id);
    }

    @Override
    public Optional<Platform> findByName(String name) {
        utilValidations.validatePlatformNameFormat(name);
        String platformName = name.toUpperCase();

        return platformRepository.findByName(platformName);
    }

    @Override
    public List<PlatformResponse> getAllPlatformsResponse() {
        return platformRepository.findAll()
                .stream()
                .map(platformResponseMapperImpl::mapFrom)
                .toList();
    }

    @Override
    public PlatformResponse addPlatForm(PlatformRequest platformRequest) {
        addPlatformValidation.validate(platformRequest);

        Platform platformEntity = platformMapperImpl.mapFrom(platformRequest);
        platformRepository.save(platformEntity);
        log.info("Saved platform {}", platformEntity.getName());

        return new PlatformResponse(platformEntity.getName());
    }

    @Override
    public Platform findPlatformByName(String platformName) {
        utilValidations.validatePlatformNameFormat(platformName);
        platformName = platformName.toUpperCase();
        log.info("Checking if {} it's an existing platform", platformName);
        Optional<Platform> platform = platformRepository.findByName(platformName);

        if (platform.isEmpty()) {
            String message = String.format(PLATFORM_NOT_FOUND, platformName);

            throw new PlatformNotFoundException(message);
        }

        return platform.get();
    }

    @Override
    public PlatformResponse updatePlatform(String platformName, PlatformRequest platformRequest) {
        addPlatformValidation.validate(platformRequest);
        Platform platform = findByName(platformName)
                .orElseThrow(() -> {
                    String message = String.format(PLATFORM_NOT_FOUND, platformName);

                    return new PlatformNotFoundException(message);
                });
        String newPlatformName = platformRequest.getName();
        platform.setName(newPlatformName);

        platformRepository.save(platform);

        log.info("Updated {} to {}", newPlatformName, platform.getName());

        return new PlatformResponse(newPlatformName);
    }

    @Override
    public void deletePlatform(String platformName) {
        Platform platform = findByName(platformName)
                .orElseThrow(() -> {
                    String message = String.format(PLATFORM_NOT_FOUND, platformName);

                    return new PlatformNotFoundException(message);
                });
        Optional<List<UserCrypto>> cryptosToDelete = userCryptoService.findAllByPlatformId(platform.getId());

        if (cryptosToDelete.isPresent() && CollectionUtils.isNotEmpty(cryptosToDelete.get())) {
            Map<String, String> cryptos = cryptosToDelete.get()
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(UserCrypto::getId, UserCrypto::getCryptoId));

            userCryptoService.deleteAllUserCryptosById(cryptos.keySet());
            log.info("Deleted {} in platform {}", cryptos.values(), platformName);
        }

        platformRepository.delete(platform);
        log.info("Deleted platform {}", platform.getName());
    }
}
