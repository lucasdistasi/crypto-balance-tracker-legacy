package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.distasilucas.cryptobalancetracker.constant.Constants.PLATFORM_NOT_FOUND_DESCRIPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final EntityMapper<Crypto, CryptoDTO> cryptoMapperImpl;
    private final EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImpl;
    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;
    private final Validation<CryptoDTO> addCryptoValidation;
    private final Validation<CryptoDTO> updateCryptoValidation;

    @Override
    public CryptoDTO addCoin(CryptoDTO cryptoDTO) {
        addCryptoValidation.validate(cryptoDTO);
        Crypto crypto = cryptoMapperImpl.mapFrom(cryptoDTO);
        cryptoRepository.save(crypto);

        CryptoDTO cryptoResponse = cryptoDTOMapperImpl.mapFrom(crypto);
        log.info("Saved Crypto {}", cryptoResponse);

        return cryptoResponse;
    }

    @Override
    public CryptoDTO updateCoin(CryptoDTO cryptoDTO, String coinId) {
        updateCryptoValidation.validate(cryptoDTO);

        Crypto crypto = cryptoRepository.findById(coinId)
                .orElseThrow(() -> new CoinNotFoundException("Coin not found"));

        Platform platform = platformRepository.findByName(cryptoDTO.platform().toUpperCase())
                .orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND_DESCRIPTION));

        crypto.setQuantity(cryptoDTO.quantity());
        crypto.setPlatformId(platform.getId());
        cryptoRepository.save(crypto);

        CryptoDTO cryptoResponse = cryptoDTOMapperImpl.mapFrom(crypto);
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
                    throw new CoinNotFoundException("Coin not found");
                });
    }
}
