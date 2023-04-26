package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final EntityMapper<Crypto, CryptoRequest> cryptoMapperImpl;
    private final EntityMapper<CryptoResponse, Crypto> cryptoResponseMapperImpl;
    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;
    private final Validation<CryptoRequest> addCryptoValidation;
    private final Validation<CryptoRequest> updateCryptoValidation;

    @Override
    public CryptoResponse getCoin(String coinId) {
        Optional<Crypto> optionalCrypto = cryptoRepository.findById(coinId);

        if (optionalCrypto.isEmpty())
            throw new CoinNotFoundException(String.format(COIN_ID_NOT_FOUND, coinId));

        Crypto crypto = optionalCrypto.get();
        Optional<Platform> platform = platformRepository.findById(crypto.getPlatformId());
        String platformName = platform.isPresent() ? platform.get().getName() : UNKNOWN;

        return CryptoResponse.builder()
                .coinId(crypto.getId())
                .coinName(crypto.getName())
                .platform(platformName)
                .quantity(crypto.getQuantity())
                .build();
    }

    @Override
    public Optional<List<CryptoResponse>> getCoins() {
        List<Crypto> cryptos = cryptoRepository.findAll();

        if (CollectionUtils.isEmpty(cryptos)) return Optional.empty();

        return Optional.of(
                cryptos.stream()
                        .map(cryptoResponseMapperImpl::mapFrom)
                        .toList()
        );
    }

    @Override
    public CryptoResponse addCoin(CryptoRequest cryptoRequest) {
        // TODO - validate crypto and platform inputs
        addCryptoValidation.validate(cryptoRequest);
        Crypto crypto = cryptoMapperImpl.mapFrom(cryptoRequest);
        cryptoRepository.save(crypto);

        CryptoResponse cryptoResponse = cryptoResponseMapperImpl.mapFrom(crypto);
        log.info("Saved Crypto {}", cryptoResponse);

        return cryptoResponse;
    }

    @Override
    public CryptoResponse updateCoin(CryptoRequest cryptoRequest, String coinId) {
        updateCryptoValidation.validate(cryptoRequest);

        Crypto crypto = cryptoRepository.findById(coinId)
                .orElseThrow(() -> new CoinNotFoundException(COIN_NOT_FOUND));

        Platform platform = platformRepository.findByName(cryptoRequest.platform().toUpperCase())
                .orElseThrow(() -> {
                    String message = String.format(PLATFORM_NOT_FOUND, cryptoRequest.platform());

                    return new PlatformNotFoundException(message);
                });

        crypto.setQuantity(cryptoRequest.quantity());
        crypto.setPlatformId(platform.getId());
        cryptoRepository.save(crypto);

        CryptoResponse cryptoResponse = cryptoResponseMapperImpl.mapFrom(crypto);
        log.info("Updated Crypto {}", cryptoResponse);

        return cryptoResponse;
    }

    @Override
    public void deleteCoin(String coinId) {
        cryptoRepository.findById(coinId)
                .ifPresentOrElse(crypto -> {
                    log.info("Deleted crypto [{}] in platform id [{}]", crypto.getName(), crypto.getPlatformId());

                    cryptoRepository.delete(crypto);
                }, () -> {
                    throw new CoinNotFoundException(COIN_NOT_FOUND);
                });
    }
}
