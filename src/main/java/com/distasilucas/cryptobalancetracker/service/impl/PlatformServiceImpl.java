package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.DuplicatedPlatformCoinException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.request.PlatformDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.PlatformBalanceResponse;
import com.distasilucas.cryptobalancetracker.model.response.PlatformInfo;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.distasilucas.cryptobalancetracker.constant.Constants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.Constants.NO_COIN_IN_PLATFORM;
import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;
    private final CryptoRepository cryptoRepository;
    private final Validation<PlatformDTO> addPlatformValidation;
    private final Validation<CryptoDTO> updateCryptoValidation;
    private final EntityMapper<Platform, PlatformDTO> platformMapperImpl;
    private final EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImpl;
    private final EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImpl;

    @Override
    public PlatformDTO addPlatForm(PlatformDTO platformDTO) {
        addPlatformValidation.validate(platformDTO);

        Platform platformEntity = platformMapperImpl.mapFrom(platformDTO);
        platformRepository.save(platformEntity);
        log.info("Saved platform {}", platformEntity.getName());

        return platformDTO;
    }

    @Override
    public Optional<PlatformBalanceResponse> getPlatformsBalances() {
        List<Crypto> cryptos = cryptoRepository.findAll();
        CryptoBalanceResponse cryptoBalanceResponse = cryptoBalanceResponseMapperImpl.mapFrom(cryptos);
        List<CoinResponse> coins = cryptoBalanceResponse.getCoins();

        if (CollectionUtils.isNotEmpty(coins)) {
            Map<String, BigDecimal> balancePerPlatform = new HashMap<>();

            coins
                    .forEach(coin -> {
                        String platform = coin.getPlatform();
                        BigDecimal balance = coin.getBalance();

                        balancePerPlatform.compute(platform, (k, v) -> (v == null) ? balance : v.add(balance));
                    });

            List<PlatformInfo> platforms = getPlatformInfo(coins);
            PlatformBalanceResponse platformBalanceResponse = PlatformBalanceResponse.builder()
                    .platforms(platforms)
                    .totalBalance(cryptoBalanceResponse.getTotalBalance())
                    .build();

            return Optional.of(platformBalanceResponse);
        }

        return Optional.empty();
    }

    @Override
    public PlatformDTO updatePlatform(PlatformDTO platformDTO, String platformName) {
        addPlatformValidation.validate(platformDTO);
        Platform platform = findPlatformByName(platformName);
        String newPlatformName = platformDTO.getName();
        platform.setName(newPlatformName);

        platformRepository.save(platform);

        log.info("Updated {} to {}", newPlatformName, platform.getName());

        return platformDTO;
    }

    @Override
    public CryptoDTO updatePlatformCoin(CryptoDTO cryptoDTO, String platformName, String coinId) {
        updateCryptoValidation.validate(cryptoDTO);
        Platform platform = findPlatformByName(platformName);
        Optional<Crypto> optionalExistingCrypto = cryptoRepository.findByCoinIdAndPlatformId(coinId, platform.getId());

        if (optionalExistingCrypto.isEmpty()) {
            String message = String.format(NO_COIN_IN_PLATFORM, coinId, platformName);

            throw new CoinNotFoundException(message);
        }

        Crypto existingCrypto = optionalExistingCrypto.get();
        Platform newPlatform = findPlatformByName(cryptoDTO.platform());
        Optional<Crypto> newCrypto = cryptoRepository.findByNameAndPlatformId(existingCrypto.getName(), newPlatform.getId());

        if (newCrypto.isPresent() && isDifferentPlatform(platformName, newPlatform)) {
            String message = String.format(DUPLICATED_PLATFORM_COIN, coinId, newPlatform.getName());

            throw new DuplicatedPlatformCoinException(message);
        }

        existingCrypto.setQuantity(cryptoDTO.quantity());
        existingCrypto.setPlatformId(newPlatform.getId());
        cryptoRepository.save(existingCrypto);

        CryptoDTO cryptoResponse = cryptoDTOMapperImpl.mapFrom(existingCrypto);
        log.info("Updated coin {} to {}", cryptoDTO, cryptoResponse);

        return cryptoResponse;
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

    @Override
    public void deletePlatformCoin(String platformName, String coinId) {
        Optional<Platform> optionalPlatform = platformRepository.findByName(platformName.toUpperCase());

        if (optionalPlatform.isEmpty()) {
            String message = String.format(PLATFORM_NOT_FOUND, platformName);

            throw new PlatformNotFoundException(message);
        }

        Platform platform = optionalPlatform.get();
        cryptoRepository.findByCoinIdAndPlatformId(coinId, platform.getId())
                .ifPresentOrElse(crypto -> {
                    log.info("Deleted coin {} from {}", crypto.getName(), platform.getName());

                    cryptoRepository.delete(crypto);
                }, () -> {
                    String message = String.format(NO_COIN_IN_PLATFORM, coinId, platform.getName());

                    throw new CoinNotFoundException(message);
                });
    }

    @Override
    public Optional<CryptoBalanceResponse> getAllCoins(String platformName) {
        log.info("Retrieving coins in platform {}", platformName);
        Platform platform = findPlatformByName(platformName);
        Optional<List<Crypto>> cryptos = cryptoRepository.findAllByPlatformId(platform.getId());

        Optional<CryptoBalanceResponse> cryptoBalanceResponse = Optional.empty();

        if (cryptos.isPresent() && CollectionUtils.isNotEmpty(cryptos.get())) {
            cryptoBalanceResponse = Optional.of(cryptoBalanceResponseMapperImpl.mapFrom(cryptos.get()));
        }

        return cryptoBalanceResponse;
    }

    @Override
    public Platform findPlatformByName(String platformName) {
        platformName = platformName.toUpperCase();
        log.info("Checking if {} it's an existing platform", platformName);
        Optional<Platform> platform = platformRepository.findByName(platformName);

        if (platform.isEmpty()) {
            String message = String.format(PLATFORM_NOT_FOUND, platformName);

            throw new PlatformNotFoundException(message);
        }

        return platform.get();
    }

    private static boolean isDifferentPlatform(String platformName, Platform newPlatform) {
        return !platformName.equalsIgnoreCase(newPlatform.getName());
    }

    private List<PlatformInfo> getPlatformInfo(List<CoinResponse> coins) {
        return coins.stream()
                .map(coinResponse -> PlatformInfo.builder()
                        .platformName(coinResponse.getPlatform())
                        .percentage(coinResponse.getPercentage())
                        .balance(coinResponse.getBalance())
                        .build())
                .toList();
    }
}
