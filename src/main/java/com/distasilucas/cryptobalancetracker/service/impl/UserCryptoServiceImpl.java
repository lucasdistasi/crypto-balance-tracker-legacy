package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.UserCryptoService;
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
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCryptoServiceImpl implements UserCryptoService {

    private final UtilValidations utilValidations;
    private final CryptoServiceImpl cryptoServiceImpl;
    private final EntityMapper<UserCrypto, AddCryptoRequest> cryptoMapperImpl;
    private final EntityMapper<UserCryptoResponse, UserCrypto> userCryptoResponseMapperImpl;
    private final CryptoRepository cryptoRepository;
    private final UserCryptoRepository userCryptoRepository;
    private final PlatformRepository platformRepository;
    private final Validation<AddCryptoRequest> addCryptoValidation;
    private final Validation<UpdateCryptoRequest> updateCryptoValidation;

    @Override
    public UserCryptoResponse getCrypto(String id) {
        utilValidations.validateIdMongoEntityFormat(id);
        Optional<UserCrypto> optionalUserCrypto = userCryptoRepository.findById(id);

        if (optionalUserCrypto.isEmpty())
            throw new CryptoNotFoundException(String.format(CRYPTO_ID_NOT_FOUND, id));

        UserCrypto userCrypto = optionalUserCrypto.get();
        Optional<Platform> platform = platformRepository.findById(userCrypto.getPlatformId());
        String platformName = platform.isPresent() ? platform.get().getName() : UNKNOWN;
        Crypto crypto = cryptoRepository.findById(userCrypto.getCryptoId())
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));

        return UserCryptoResponse.builder()
                .id(userCrypto.getId())
                .cryptoName(crypto.getName())
                .platform(platformName)
                .quantity(userCrypto.getQuantity())
                .build();
    }

    @Override
    public Optional<PageCryptoResponse> getCryptos(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<UserCrypto> cryptos = userCryptoRepository.findAll(pageable);

        if (cryptos.isEmpty()) return Optional.empty();

        List<UserCryptoResponse> cryptosResponse = cryptos.stream()
                .map(userCryptoResponseMapperImpl::mapFrom)
                .toList();

        PageCryptoResponse pageCryptoResponse = new PageCryptoResponse(page, cryptos.getTotalPages(), cryptosResponse);

        return Optional.of(pageCryptoResponse);
    }

    @Override
    public UserCryptoResponse saveUserCrypto(AddCryptoRequest addCryptoRequest) {
        addCryptoValidation.validate(addCryptoRequest);
        UserCrypto userCrypto = cryptoMapperImpl.mapFrom(addCryptoRequest);
        userCryptoRepository.save(userCrypto);
        cryptoServiceImpl.saveCryptoIfNotExists(userCrypto.getCryptoId());

        UserCryptoResponse userCryptoResponse = userCryptoResponseMapperImpl.mapFrom(userCrypto);
        log.info("Saved Crypto {}", userCryptoResponse);

        return userCryptoResponse;
    }

    @Override
    public UserCryptoResponse updateCrypto(UpdateCryptoRequest updateCryptoRequest, String id) {
        updateCryptoRequest.setCryptoId(id);
        updateCryptoValidation.validate(updateCryptoRequest);

        UserCrypto crypto = userCryptoRepository.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));

        Platform platform = platformRepository.findByName(updateCryptoRequest.getPlatform().toUpperCase())
                .orElseThrow(() -> {
                    String message = String.format(PLATFORM_NOT_FOUND, updateCryptoRequest.getPlatform());

                    return new PlatformNotFoundException(message);
                });

        crypto.setQuantity(updateCryptoRequest.getQuantity());
        crypto.setPlatformId(platform.getId());
        userCryptoRepository.save(crypto);

        UserCryptoResponse userCryptoResponse = userCryptoResponseMapperImpl.mapFrom(crypto);
        log.info("Updated Crypto {}", userCryptoResponse);

        return userCryptoResponse;
    }

    @Override
    public void deleteCrypto(String id) {
        utilValidations.validateIdMongoEntityFormat(id);
        userCryptoRepository.findById(id)
                .ifPresentOrElse(userCrypto -> {
                    log.info("Deleted cryptoId [{}] in platform id [{}]", userCrypto.getCryptoId(), userCrypto.getPlatformId());

                    userCryptoRepository.delete(userCrypto);
                }, () -> {
                    throw new CryptoNotFoundException(CRYPTO_NOT_FOUND);
                });
    }
}
