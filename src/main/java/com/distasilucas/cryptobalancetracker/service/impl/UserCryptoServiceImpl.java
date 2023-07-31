package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.response.crypto.PageCryptoResponse;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
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

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCryptoServiceImpl implements UserCryptoService {

    private final UtilValidations utilValidations;
    private final CryptoService cryptoService;
    private final EntityMapper<UserCrypto, AddCryptoRequest> cryptoMapperImpl;
    private final EntityMapper<UserCryptoResponse, UserCrypto> userCryptoResponseMapperImpl;
    private final UserCryptoRepository userCryptoRepository;
    private final PlatformService platformService;
    private final Validation<AddCryptoRequest> addCryptoValidation;
    private final Validation<UpdateCryptoRequest> updateCryptoValidation;

    @Override
    public Optional<List<UserCrypto>> findAllByCryptoId(String cryptoId) {
        return userCryptoRepository.findAllByCryptoId(cryptoId);
    }

    @Override
    public Optional<UserCrypto> findById(String id) {
        return userCryptoRepository.findById(id);
    }

    @Override
    public Optional<UserCrypto> findByCryptoIdAndPlatformId(String cryptoId, String platformId) {
        return userCryptoRepository.findByCryptoIdAndPlatformId(cryptoId, platformId);
    }

    @Override
    public List<UserCrypto> findAll() {
        return userCryptoRepository.findAll();
    }

    @Override
    public Optional<List<UserCrypto>> findAllByPlatformId(String platformId) {
        return userCryptoRepository.findAllByPlatformId(platformId);
    }

    @Override
    public void saveUserCrypto(UserCrypto userCrypto) {
        userCryptoRepository.save(userCrypto);
    }

    @Override
    public void saveAll(List<UserCrypto> userCryptos) {
        userCryptoRepository.saveAll(userCryptos);
    }

    @Override
    public UserCryptoResponse getUserCryptoResponse(String id) {
        utilValidations.validateIdMongoEntityFormat(id);
        Optional<UserCrypto> optionalUserCrypto = userCryptoRepository.findById(id);

        if (optionalUserCrypto.isEmpty())
            throw new CryptoNotFoundException(String.format(CRYPTO_ID_NOT_FOUND, id));

        UserCrypto userCrypto = optionalUserCrypto.get();
        Platform platform = platformService.findById(userCrypto.getPlatformId())
                .orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND));
        Crypto crypto = cryptoService.findById(userCrypto.getCryptoId())
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));

        return UserCryptoResponse.builder()
                .id(userCrypto.getId())
                .cryptoName(crypto.getName())
                .platform(platform.getName())
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
        cryptoService.saveCryptoIfNotExists(userCrypto.getCryptoId());

        UserCryptoResponse userCryptoResponse = userCryptoResponseMapperImpl.mapFrom(userCrypto);
        log.info("Saved Crypto {}", userCryptoResponse);

        return userCryptoResponse;
    }

    @Override
    public UserCryptoResponse updateUserCrypto(UpdateCryptoRequest updateCryptoRequest, String id) {
        updateCryptoRequest.setCryptoId(id);
        updateCryptoValidation.validate(updateCryptoRequest);

        UserCrypto crypto = userCryptoRepository.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));

        Platform platform = platformService.findByName(updateCryptoRequest.getPlatform().toUpperCase())
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
    public void deleteUserCrypto(String id) {
        utilValidations.validateIdMongoEntityFormat(id);
        userCryptoRepository.findById(id)
                .ifPresentOrElse(userCrypto -> {
                    log.info("Deleted cryptoId {} in platform id {}", userCrypto.getCryptoId(), userCrypto.getPlatformId());

                    userCryptoRepository.delete(userCrypto);
                    cryptoService.deleteCryptoIfNotUsed(userCrypto.getCryptoId());
                }, () -> {
                    throw new CryptoNotFoundException(CRYPTO_NOT_FOUND);
                });
    }

    @Override
    public void deleteUserCrypto(UserCrypto userCrypto) {
        userCryptoRepository.delete(userCrypto);
        cryptoService.deleteCryptoIfNotUsed(userCrypto.getCryptoId());
    }
}
