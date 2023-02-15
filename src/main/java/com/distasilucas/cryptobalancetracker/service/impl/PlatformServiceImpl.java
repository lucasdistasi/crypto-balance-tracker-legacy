package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;
    private final CryptoRepository cryptoRepository;
    private final Validation<PlatformDTO> addPlatformValidation;
    private final EntityMapper<Platform, PlatformDTO> platformMapperImpl;
    private final EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImpl;

    @Override
    public PlatformDTO addPlatForm(PlatformDTO platformDTO) {
        addPlatformValidation.validate(platformDTO);

        Platform platformEntity = platformMapperImpl.mapFrom(platformDTO);
        platformRepository.save(platformEntity);
        log.info("Saved platform {}", platformEntity.getName());

        return platformDTO;
    }

    @Override
    public Platform findPlatform(String platformName) {
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
    public PlatformDTO updatePlatform(PlatformDTO platformDTO, String platformName) {
        Platform platform = findPlatform(platformName);
        String newPlatformName = platformDTO.getName();
        platform.setName(newPlatformName);

        platformRepository.save(platform);

        log.info("Updated {} to {}", newPlatformName, platform.getName());

        return platformDTO;
    }

    @Override
    public void deletePlatform(String platformName) {
        Platform platform = findPlatform(platformName);
        Optional<List<Crypto>> cryptos = cryptoRepository.findAllByPlatform(platform);

        if (cryptos.isPresent() && CollectionUtils.isNotEmpty(cryptos.get())) {
            List<String> cryptoIds = cryptos.get().stream()
                    .map(Crypto::getName)
                    .toList();

            cryptoRepository.deleteAllById(cryptoIds);
            log.info("Deleted {} in platform {}", cryptoIds, platformName);
        }

        platformRepository.delete(platform);
        log.info("Deleted platform {}", platform.getName());
    }

    @Override
    public Optional<CryptoBalanceResponse> getAllCoins(String platformName) {
        log.info("Retrieving coins in platform {}", platformName);
        Platform platform = findPlatform(platformName);
        Optional<List<Crypto>> cryptos = cryptoRepository.findAllByPlatform(platform);

        Optional<CryptoBalanceResponse> cryptoBalanceResponse = Optional.empty();

        if (cryptos.isPresent() && CollectionUtils.isNotEmpty(cryptos.get())) {
            cryptoBalanceResponse = Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(cryptos.get()));
        }

        return cryptoBalanceResponse;
    }
}
