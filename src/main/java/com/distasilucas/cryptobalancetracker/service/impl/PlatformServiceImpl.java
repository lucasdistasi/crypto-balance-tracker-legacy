package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformRequest;
import com.distasilucas.cryptobalancetracker.model.response.platform.PlatformResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {

    private final UtilValidations utilValidations;
    private final PlatformRepository platformRepository;
    private final CryptoRepository cryptoRepository;
    private final Validation<PlatformRequest> addPlatformValidation;
    private final EntityMapper<Platform, PlatformRequest> platformMapperImpl;
    private final EntityMapper<PlatformResponse, Platform> platformResponseMapperImpl;

    @Override
    public List<PlatformResponse> getAllPlatforms() {
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

        Platform platform = findPlatformByName(platformName);
        String newPlatformName = platformRequest.getName();
        platform.setName(newPlatformName);

        platformRepository.save(platform);

        log.info("Updated {} to {}", newPlatformName, platform.getName());

        return new PlatformResponse(newPlatformName);
    }

    @Override
    public void deletePlatform(String platformName) {
        Platform platform = findPlatformByName(platformName);
        Optional<List<Crypto>> cryptos = cryptoRepository.findAllByPlatformId(platform.getId());

        if (cryptos.isPresent() && CollectionUtils.isNotEmpty(cryptos.get())) {
            Map<String, String> cryptoIds = cryptos.get()
                    .stream()
                    .collect(Collectors.toMap(Crypto::getId, Crypto::getName));

            cryptoRepository.deleteAllById(cryptoIds.keySet());
            log.info("Deleted {} in platform {}", cryptoIds.values(), platformName);
        }

        platformRepository.delete(platform);
        log.info("Deleted platform {}", platform.getName());
    }
}
