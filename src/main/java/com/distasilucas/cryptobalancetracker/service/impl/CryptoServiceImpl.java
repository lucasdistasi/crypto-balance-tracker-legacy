package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.UtilValidations;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final UtilValidations utilValidations;
    private final EntityMapper<Crypto, AddCryptoRequest> cryptoMapperImpl;
    private final EntityMapper<CryptoResponse, Crypto> cryptoResponseMapperImpl;
    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;
    private final Validation<AddCryptoRequest> addCryptoValidation;
    private final Validation<UpdateCryptoRequest> updateCryptoValidation;

    @Override
    public CryptoResponse getCoin(String coinId) {
        utilValidations.validateIdMongoEntityFormat(coinId);
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
    public Optional<PageCryptoResponse> getCoins(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Crypto> cryptos = cryptoRepository.findAll(pageable);

        if (cryptos.isEmpty()) return Optional.empty();

        List<CryptoResponse> cryptosResponse = cryptos.stream()
                .map(cryptoResponseMapperImpl::mapFrom)
                .toList();

        PageCryptoResponse pageCryptoResponse = new PageCryptoResponse(page, cryptos.getTotalPages(), cryptosResponse);

        return Optional.of(pageCryptoResponse);
    }

    @Override
    public CryptoResponse addCoin(AddCryptoRequest addCryptoRequest) {
        addCryptoValidation.validate(addCryptoRequest);
        Crypto crypto = cryptoMapperImpl.mapFrom(addCryptoRequest);
        cryptoRepository.save(crypto);

        CryptoResponse cryptoResponse = cryptoResponseMapperImpl.mapFrom(crypto);
        log.info("Saved Crypto {}", cryptoResponse);

        return cryptoResponse;
    }

    @Override
    public CryptoResponse updateCoin(UpdateCryptoRequest updateCryptoRequest, String coinId) {
        updateCryptoRequest.setCryptoId(coinId);
        updateCryptoValidation.validate(updateCryptoRequest);

        Crypto crypto = cryptoRepository.findById(coinId)
                .orElseThrow(() -> new CoinNotFoundException(COIN_NOT_FOUND));

        Platform platform = platformRepository.findByName(updateCryptoRequest.getPlatform().toUpperCase())
                .orElseThrow(() -> {
                    String message = String.format(PLATFORM_NOT_FOUND, updateCryptoRequest.getPlatform());

                    return new PlatformNotFoundException(message);
                });

        crypto.setQuantity(updateCryptoRequest.getQuantity());
        crypto.setPlatformId(platform.getId());
        cryptoRepository.save(crypto);

        CryptoResponse cryptoResponse = cryptoResponseMapperImpl.mapFrom(crypto);
        log.info("Updated Crypto {}", cryptoResponse);

        return cryptoResponse;
    }

    @Override
    public void deleteCoin(String coinId) {
        utilValidations.validateIdMongoEntityFormat(coinId);
        cryptoRepository.findById(coinId)
                .ifPresentOrElse(crypto -> {
                    log.info("Deleted crypto [{}] in platform id [{}]", crypto.getName(), crypto.getPlatformId());

                    cryptoRepository.delete(crypto);
                }, () -> {
                    throw new CoinNotFoundException(COIN_NOT_FOUND);
                });
    }
}
